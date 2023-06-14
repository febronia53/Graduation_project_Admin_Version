package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.uni.uniadmin.R
import com.uni.uniadmin.adapters.StudentAdapter
import com.uni.uniadmin.classes.Assistant
import com.uni.uniadmin.classes.Courses
import com.uni.uniadmin.classes.Professor
import com.uni.uniadmin.classes.user.UserStudent
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.data.di.PostType.course
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FireStorageViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddCourseFragment : Fragment() {

    private lateinit var prof:String
    private lateinit var grade:String
    private lateinit var assistant:String
    private val viewModelAuth : AuthViewModel by viewModels()
    private val viewModel : FirebaseViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    private lateinit var profList: MutableList<String>
    private lateinit var assistantList: MutableList<String>
    var assistantIndex:Int = 0
    var lecturer:Int = 0
    private lateinit var profListData: MutableList<Professor>
    private lateinit var assistantListData: MutableList<Assistant>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser= UserAdmin()
        viewModelAuth.getSessionStudent {user->
            if (user != null){
                currentUser = user
            }else
            {
                Toast.makeText(context,"there is an error on loading user data", Toast.LENGTH_SHORT).show()
            }

        }
        val view = inflater.inflate(R.layout.fragment_add_course, container, false)
        val add = view.findViewById<Button>(R.id.add_course)
        val profText=view.findViewById<TextView>(R.id.professor_text)
        val courseID=view.findViewById<EditText>(R.id.course_id)
        val gradeText=view.findViewById<TextView>(R.id.grade_spinner_add_course_text)

        val assistantText=view.findViewById<TextView>(R.id.assistant_text)
        assistant=""
        prof=""
        grade=""


        viewModel.getAllProfessor()
        observeProfessor()
        viewModel.getAllAssistant()
        observeAssistant()


add.setOnClickListener {
    val cID= courseID.text.toString()
   if (lecturer>0 && assistantIndex>0&& grade.isNotEmpty()&&cID.isNotEmpty()){
       // TODO: we need a button to navigate to the courses list screen
       //TODO() the grade is here as the admin is assigned to some grade but we generated the grade by spinner
       viewModel.addCourse(Courses(cID,grade,profListData[lecturer].code,assistantListData[assistantIndex].code),profListData[lecturer],assistantListData[assistantIndex])
       observeAddedCourse()
   }else{
Toast.makeText(context,"make sure to fill all data",Toast.LENGTH_SHORT).show()
   }
}


        val adapter2: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, assistantList
        )

        val autoCom2 = view.findViewById<Spinner>(R.id.professor_spinner)
        autoCom2.adapter = adapter2

        autoCom2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                assistant =assistantList[p2]
                assistantIndex = p2
                Toast.makeText(requireContext(),p2,Toast.LENGTH_SHORT).show()
                assistantText.text=assistant
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val gradeList = resources.getStringArray(R.array.grades)
        val adapterGrade:ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.grades,R.layout.spinner_item)

        val autoComGrade = view.findViewById<Spinner>(R.id.grade_spinner_add_course)
        autoComGrade.adapter = adapterGrade

        autoComGrade.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {

                Toast.makeText(requireContext(),p2,Toast.LENGTH_SHORT).show()
                grade =gradeList[p2]
                gradeText.text=grade
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, profList
        )

        val autoCom = view.findViewById<Spinner>(R.id.professor_spinner)
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                lecturer = p2
                Toast.makeText(requireContext(),p2,Toast.LENGTH_SHORT).show()
                prof =profList[p2]
                profText.text=prof
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        return view
    }

    private fun observeAddedCourse() {
        lifecycleScope.launchWhenCreated {
            viewModel.addCourse.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Toast.makeText(context, "course added successfully", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }}


    private fun observeAssistant() {
        lifecycleScope.launchWhenCreated {
            viewModel.getAllAssistant.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        assistantList.clear()
                        assistantListData.clear()
                        it.result.forEach {
                            assistantList.add("${it.name} ${it.code}")
                        assistantListData.add(it)
                        }
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }}


    private fun observeProfessor() {
        lifecycleScope.launchWhenCreated {
            viewModel.getAllProfessor.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        profList.clear()
                        profListData.clear()
                        it.result.forEach {
                        profList.add("${it.name} ${it.code}")
                            profListData.add(it)
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
    }
/*
    private fun observeCourses() {
        lifecycleScope.launchWhenCreated {
            viewModel.getCoursesByGrade.collectLatest {
                when(it)
                {
                    is Resource.Failure -> Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    Resource.Loading -> {}
                    is Resource.Success -> {

                    }
                    else->{}
                }
            }
        }
    }
*/
