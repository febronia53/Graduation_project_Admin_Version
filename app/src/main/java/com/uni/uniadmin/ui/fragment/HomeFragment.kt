package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.Courses
import com.uni.uniadmin.classes.Posts
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.data.di.PostType
import com.uni.uniadmin.ui.HomeScreen
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.adapters.PostsAdapter
import com.uni.uniteaching.classes.user.UserAdmin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


class HomeFragment : Fragment() {

    private val viewModel: FirebaseViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    lateinit var progress: ProgressBar
    lateinit var currentUser: UserAdmin
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var studentID:EditText
    lateinit  var section:String
    lateinit var department:String
    lateinit var coursesList:MutableList<Courses>

    lateinit var  adapter : PostsAdapter
    lateinit var postsList:MutableList<Posts>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// update user data --------------------------------------------------------------------------------
        currentUser= UserAdmin()
        authViewModel.getSessionStudent {user->
            if (user != null){
                currentUser = user
            }else
            {
                Toast.makeText(context,"there is an error on loading user data", Toast.LENGTH_SHORT).show()
            }

        }
        // update user data --------------------------------------------------------------------------------

        val view= inflater.inflate(R.layout.fragment_home, container, false)
        val sectionText = view.findViewById<TextView>(R.id.section_text)
        val departmentText = view.findViewById<TextView>(R.id.department_text_home)
        val searchStudent = view.findViewById<Button>(R.id.search_student_home)
        val searchSection = view.findViewById<Button>(R.id.search_section_home)
        studentID = view.findViewById<EditText>(R.id.student_ID_home)

        var section =""
        var department=""

        searchStudent.setOnClickListener {
            val id = studentID.text.toString()
            if (id.isNotEmpty()){
                viewModel.getPostsPersonal(id)
                observe()
            }else {
                Toast.makeText(context, "make sure to type the student ID", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        searchSection.setOnClickListener {
            if (section.isNotEmpty()&&department.isNotEmpty()){
                viewModel.getPostsSection(section,department)
                observe()
            }
        }
        val departmentList = resources.getStringArray(R.array.departement)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.departement,R.layout.spinner_item)
        val autoCom = view.findViewById<Spinner>(R.id.departement_spinner_home)
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                department =departmentList[p2]
                departmentText.text=department}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val sectionList = resources.getStringArray(R.array.Section)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.Section,R.layout.spinner_item)
        val autoCom2 = view.findViewById<Spinner>(R.id.section_spinner_home)
        autoCom2.adapter = adapter2

        autoCom2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                section =sectionList[p2]
                    sectionText.text=section}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    return  view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_recycler)
        progress = view.findViewById<ProgressBar>(R.id.progress_par_home)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)


        coursesList = arrayListOf()
        postsList = arrayListOf()


        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            if (currentUser!=null){
                viewModel.getCoursesByGrade(currentUser.grade)
            }
         }

        adapter= PostsAdapter(requireContext(),postsList,

            onItemClicked = {pos, item->
                Toast.makeText(requireContext(),item.authorName, Toast.LENGTH_SHORT).show()
            }
            ,
            onComment = { pos, item ->
                val bundle=Bundle()
                bundle.putString("postId",item.postID)
                bundle.putString("aud",item.audience)
                when(item.audience){
                    PostType.course ->{
                        bundle.putString("course",item.courseID)
                        bundle.putString("section","")
                        bundle.putString("department","")
                        bundle.putString("studentID","")
                    }
                    PostType.personal_posts ->{
                        bundle.putString("course","")
                        bundle.putString("section","")
                        bundle.putString("department","")
                        bundle.putString("studentID",studentID.text.toString())

                    }
                    PostType.section_posts ->{
                        bundle.putString("course","")
                        bundle.putString("section",section)
                        bundle.putString("department",department)
                        bundle.putString("studentID","")

                    }
                }





                val commentFragment=CommentFragment()
                commentFragment.arguments=bundle
                (activity as HomeScreen).replaceFragment(commentFragment)
                //Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_commentFragment)



            })


//-------------- setting the recycler data---------------------------//
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter=adapter
//-------------- setting the recycler data---------------------------//


        if (currentUser!=null){
            viewModel.getCoursesByGrade(currentUser.grade)
        }
        observeCourses()

    }
    private fun observeCourses() {
        lifecycleScope.launchWhenCreated {
            viewModel.getCourses.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility=View.VISIBLE

                    }
                    is Resource.Success -> {
                        coursesList.clear()
                        state.result.forEach {
                            coursesList.add(it)
                        }

                        viewModel.getPostsGeneral()
                        viewModel.getPostsCourse(coursesList)

                        progress.visibility=View.VISIBLE
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//
                        delay(200)
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//
                        progress.visibility=View.INVISIBLE
                        observe()
                        delay(200)
                        observeCoursesPost()



                    }
                    is Resource.Failure -> {
                        progress.visibility=View.INVISIBLE
                        Toast.makeText(context,state.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                    else->{}
                }
            }
        }}

    private fun observeCoursesPost() {
        lifecycleScope.launchWhenCreated {
            viewModel.getPostsCourses.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        state.result.forEach {
                            postsList.add(it)
                        }
                        adapter.update(postsList)
                    }
                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }
        }
        }

    private fun observe(){
        lifecycleScope.launchWhenCreated {
            viewModel.getPosts.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility=View.VISIBLE
                    }
                    is Resource.Success -> {
                        postsList.clear()
                        state.result.forEach {
                            postsList.add(it)
                        }
                        adapter.update(postsList)
                    }
                    is Resource.Failure -> {
                        progress.visibility=View.INVISIBLE
                        Toast.makeText(context,state.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                    else->{}
                }
            }
        }
    }}

