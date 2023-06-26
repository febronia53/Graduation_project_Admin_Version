package com.uni.uniadmin.ui.fragment.addData

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniadmin.R
import com.uni.uniadmin.adapters.StudentAdapter
import com.uni.uniadmin.classes.Posts
import com.uni.uniadmin.classes.user.UserStudent
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.data.di.PostType
import com.uni.uniadmin.ui.SignUp
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FireStorageViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.adapters.PostsAdapter
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*


@AndroidEntryPoint
class AddPostFragment : Fragment() {

    private val viewModelAuth : AuthViewModel by viewModels()
    private val fireStorageViewModel : FireStorageViewModel by viewModels()
    private val viewModel : FirebaseViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    private lateinit var department: String
    private lateinit var section: String
    private lateinit var course: String
    private lateinit var coursesList: MutableList<String>
    lateinit var  recyAdapter : StudentAdapter
    private lateinit var studentsList: MutableList<UserStudent>

    private lateinit var userImageUri: Uri
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studentsList= arrayListOf()
        currentUser= UserAdmin()
        viewModelAuth.getSessionStudent {user->
            if (user != null){
                currentUser = user
            }else
            {
                Toast.makeText(context,"there is an error on loading user data", Toast.LENGTH_SHORT).show()
            }

        }
        val view= inflater.inflate(R.layout.fragment_add_post, container, false)
        imageView=view.findViewById(R.id.post_image)

        val stuID = view.findViewById<EditText>(R.id.post_student_ID)

        val departmentText = view.findViewById<TextView>(R.id.department_post_text)
        val postText = view.findViewById<EditText>(R.id.post_description)
        val sectionText = view.findViewById<TextView>(R.id.section_post_text)
        val coursesText = view.findViewById<TextView>(R.id.course_post_text)
        val addSectionPostBt=view.findViewById<Button>(R.id.add_section_post)
        val addCoursePostBt=view.findViewById<Button>(R.id.add_post_course)
        val searchStudent=view.findViewById<Button>(R.id.search_student_post)
        val addImage=view.findViewById<Button>(R.id.add_image_post_bt)
        val addGeneralPost=view.findViewById<Button>(R.id.add_general_post)
        val recyclerView = view.findViewById<RecyclerView>(R.id.post_search_recy)

        userImageUri = Uri.EMPTY
        coursesList = arrayListOf()
        department=""
        section=""
        course=""
        recyAdapter= StudentAdapter(requireContext(),studentsList,
 removePerm = {pos, item->},
            itemClick = {pos, item->}
            ,
            addPerm = {pos, item->
                val description=postText.text.toString()
                val id=stuID.text.toString()

                if (description.isNotEmpty()&& id.isNotEmpty()){
                    if (userImageUri == Uri.EMPTY){
                        viewModel.addPostPersonal(
                            Posts(
                                description
                                ,currentUser.name,
                                currentUser.userId
                                ,"",
                                id
                                , Date()
                                ,"grade: ${currentUser.grade} dep: ${department} sec: ${section}"
                                ,PostsAdapter.WITHOUT_IMAGE),item.userId)
                        observePost(false, Uri.EMPTY)
                    }else{
                        viewModel.addPostPersonal(
                            Posts(
                                description
                                ,currentUser.name,
                                currentUser.userId
                                ,"",
                                id
                                , Date()
                                ,"grade: ${currentUser.grade} dep: ${department} sec: ${section}"
                                ,PostsAdapter.WITH_IMAGE),item.userId)
                        observePost(true, userImageUri)
                    }
                }else{
                    Toast.makeText(context,"make sure to choose all data ",Toast.LENGTH_SHORT).show()
                }
            })

        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter=recyAdapter


        searchStudent.setOnClickListener {
            val id=stuID.text.toString()
if (id.isNotEmpty()){
    viewModel.searchStudentByID(currentUser.grade,id)
observeStudents()
}else{
    Toast.makeText(context,"make sure to choose all data ",Toast.LENGTH_SHORT).show()

}
        }
        addGeneralPost.setOnClickListener {
            val description=postText.text.toString()
            if (description.isNotEmpty()){
                if (userImageUri == Uri.EMPTY){
                    viewModel.addPostGeneral(
                        Posts(
                            description
                            ,currentUser.name,
                            currentUser.userId
                            ,"",
                            ""
                            , Date()
                            ,PostType.general
                            ,PostsAdapter.WITHOUT_IMAGE))
                    observePost(false, Uri.EMPTY)
                }else{
                    viewModel.addPostGeneral(
                        Posts(
                            description
                            ,currentUser.name,
                            currentUser.userId
                            ,"",
                            ""
                            , Date()
                               ,     PostType.general
                            ,PostsAdapter.WITH_IMAGE))
                    observePost(true, userImageUri)
                }
            }else{
                Toast.makeText(context,"make sure to choose all data ",Toast.LENGTH_SHORT).show()
            }
        }
        addCoursePostBt.setOnClickListener {
            val description=postText.text.toString()
            if (course.isNotEmpty()&&description.isNotEmpty()){
                if (userImageUri == Uri.EMPTY){
                    viewModel.addPostCourse(
                        Posts(
                            description
                            ,currentUser.name,
                            currentUser.userId
                            ,"",
                            course
                            , Date()
                            ,PostType.course
                            ,PostsAdapter.WITHOUT_IMAGE),course)
                    observePost(false, Uri.EMPTY)
                }else{
                    viewModel.addPostCourse(
                        Posts(
                            description
                            ,currentUser.name,
                            currentUser.userId
                            ,"",
                            course
                            , Date()
                            ,PostType.course
                            ,PostsAdapter.WITH_IMAGE),course)
                    observePost(true, userImageUri)
                }
            }else{
                Toast.makeText(context,"make sure to choose all data ",Toast.LENGTH_SHORT).show()
            }
        }
        addImage.setOnClickListener {
            pickImageFromGallery()
        }


        addSectionPostBt.setOnClickListener {
            val description=postText.text.toString()
            //TODO any here is making an error
            if (department.isNotEmpty()&&section.isNotEmpty()&&description.isNotEmpty()){
                if (userImageUri == Uri.EMPTY){
                    viewModel.addPostSection(
                        Posts(
                            description
                            ,currentUser.name,
                            currentUser.userId
                            ,"",
                            "${section}/${department}"
                            , Date()
                            ,PostType.section_posts
                            ,PostsAdapter.WITHOUT_IMAGE),section,department)
                    observePost(false, Uri.EMPTY)
                }else{
                    viewModel.addPostSection(
                        Posts(
                            description
                            ,currentUser.name,
                            currentUser.userId
                            ,"",
                            ""
                            , Date()
                            ,PostType.section_posts
                            ,PostsAdapter.WITH_IMAGE),section,department)
                    observePost(true, userImageUri)
                }
            }else{
                Toast.makeText(context,"make sure to choose all data ",Toast.LENGTH_SHORT).show()
            }
        }
        val departmentList = resources.getStringArray(R.array.department)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.department,R.layout.spinner_item)
        val autoCom = view.findViewById<Spinner>(R.id.department_spinner_post)
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                department =departmentList[p2]
                departmentText.text=department}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        val sectionList = resources.getStringArray(R.array.Section)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.Section,R.layout.spinner_item)
        val autoCom2 = view.findViewById<Spinner>(R.id.section_spinner_post)
        autoCom2.adapter = adapter2

        autoCom2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                section =sectionList[p2]
                sectionText.text=section}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val adapter3: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, coursesList
        )

        val autoCom3 = view.findViewById<Spinner>(R.id.course_post)
        autoCom3.adapter = adapter3

        autoCom3.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                course =coursesList[p2]
                coursesText.text=course
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

viewModel.getCoursesByGrade(currentUser.grade)
observeCourses()
        return view
    }

    private fun observeStudents() {
        lifecycleScope.launchWhenCreated {
            viewModel.searchStudentByID.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        studentsList.clear()
                     studentsList.add(it.result)
                    recyAdapter.update(studentsList)}
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }
    }

    private fun observePost(hasImage:Boolean,uri:Uri){
        if (hasImage){
            lifecycleScope.launchWhenCreated {
                viewModel.addPost.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                          fireStorageViewModel.addPostUri(it.result,uri)
                            observePostImage()
                        }
                        is Resource.Failure -> {
                            Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                        }
                        else->{}
                    }
                }
            }
        }else{
            lifecycleScope.launchWhenCreated {
                viewModel.addPost.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            Toast.makeText(context,"post added successfully",Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Failure -> {
                            Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                        }
                        else->{}
                    }
                }
            }
        }
    }

    private fun observePostImage() {
        lifecycleScope.launchWhenCreated {
            fireStorageViewModel.addPostUri.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Toast.makeText(context,"post added successfully",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }
    }

    private fun observeCourses() {
        lifecycleScope.launchWhenCreated {
            viewModel.getCoursesByGrade.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        coursesList.clear()
                        it.result.forEach {
                            coursesList.add(it.courseCode)
                        }
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }
    }

    private fun pickImageFromGallery(){
        val intent = Intent (Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SignUp.IMAGE_REQUEST_CODE)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUp.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK)
        {
            userImageUri = data?.data!!
            imageView.setImageURI(userImageUri)
        }
    }


}