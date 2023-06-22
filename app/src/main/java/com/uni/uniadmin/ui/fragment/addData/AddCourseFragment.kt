package com.uni.uniadmin.ui.fragment.addData

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.uni.uniadmin.R
import com.uni.uniadmin.adapters.SpinnerItemAdapter
import com.uni.uniadmin.classes.Assistant
import com.uni.uniadmin.classes.Courses
import com.uni.uniadmin.classes.Professor
import com.uni.uniadmin.classes.SpinnerItem
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.ui.HomeScreen
import com.uni.uniadmin.ui.fragment.ViewCoursesFragment
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddCourseFragment : Fragment() {

    private lateinit var prof: String
    private lateinit var grade: String
    private lateinit var assistant: String
    private val viewModelAuth: AuthViewModel by viewModels()
    private val viewModel: FirebaseViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    private lateinit var profList: MutableList<SpinnerItem>
    private lateinit var assistantList: MutableList<SpinnerItem>
    var assistantIndex: Int = 0
    var lecturer: Int = 0
    private lateinit var profListData: MutableList<Professor>
    private lateinit var assistantListData: MutableList<Assistant>
    private lateinit var adapter2: SpinnerItemAdapter
    private lateinit var adapterLecturer: SpinnerItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profList = arrayListOf()
        assistantList = arrayListOf()
        profListData = arrayListOf()
        currentUser = UserAdmin()
        assistantListData = arrayListOf()
        viewModelAuth.getSessionStudent { user ->
            if (user != null) {
                currentUser = user
            } else {
                Toast.makeText(
                    context,
                    "there is an error on loading user data",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        val view = inflater.inflate(R.layout.fragment_add_course, container, false)
        val add = view.findViewById<Button>(R.id.add_course)
        val courseID = view.findViewById<EditText>(R.id.course_id)
        val courseName = view.findViewById<EditText>(R.id.course_name)

        val viewCourse = view.findViewById<Button>(R.id.view_courses_bt)

        val gradeText = view.findViewById<TextView>(R.id.grade_spinner_add_course_text)

        assistant = ""
        prof = ""
        grade = ""

        viewModel.getAllAssistant()
        observeAssistant()
        viewModel.getAllProfessor()
        observeProfessor()

        viewCourse.setOnClickListener {
            val viewCourse = ViewCoursesFragment()
            (activity as HomeScreen).replaceFragment(viewCourse)
        }
        add.setOnClickListener {
            val cID = courseID.text.toString()
            val _courseName = courseName.text.toString()
            if (lecturer > 0 && assistantIndex > 0 && grade.isNotEmpty() && cID.isNotEmpty() && _courseName.isNotEmpty()) {

                viewModel.addCourse(
                    Courses(
                        _courseName,
                        cID,
                        grade,
                        profListData[lecturer].name,
                        assistantListData[assistantIndex].name, profListData[lecturer].code,
                        assistantListData[assistantIndex].code
                    ),
                    profListData[lecturer],
                    assistantListData[assistantIndex]
                )
                observeAddedCourse()
            } else {
                Toast.makeText(context, "make sure to fill all data", Toast.LENGTH_SHORT).show()
            }
        }


        adapter2 = SpinnerItemAdapter(
            requireContext(),
            assistantList
        )

        val autoCom2 = view.findViewById<Spinner>(R.id.assistant_spinner)
        autoCom2.adapter = adapter2

        autoCom2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                assistant = assistantList[p2].textUpperLeft
                assistantIndex = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val gradeList = resources.getStringArray(R.array.grades)
        val adapterGrade: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(), R.array.grades, R.layout.spinner_item)

        val autoComGrade = view.findViewById<Spinner>(R.id.grade_spinner_add_course)
        autoComGrade.adapter = adapterGrade

        autoComGrade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                grade = gradeList[p2]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        adapterLecturer = SpinnerItemAdapter(
            requireContext(),
            profList
        )

        val autoCom = view.findViewById<Spinner>(R.id.professor_spinner)
        autoCom.adapter = adapterLecturer

        autoCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                lecturer = p2
                prof = profList[p2].textUpperLeft
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        view.findViewById<ImageButton>(R.id.back_fragment_btn).setOnClickListener {  finishFragment()}
        return view
    }
    private fun finishFragment() {

        parentFragmentManager.popBackStack()
    }
    private fun observeAddedCourse() {
        lifecycleScope.launchWhenCreated {
            viewModel.addCourse.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Toast.makeText(context, "course added successfully", Toast.LENGTH_SHORT)
                            .show()
                        val viewCourse = ViewCoursesFragment()
                        (activity as HomeScreen).replaceFragment(viewCourse)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }


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
                            assistantList.add(SpinnerItem(it.name, it.code, it.Specialization))
                            assistantListData.add(it)
                        }
                        adapter2.notifyDataSetChanged()
                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }


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
                            profList.add(SpinnerItem(it.name, it.code, it.Specialization))
                            profListData.add(it)
                        }
                        adapterLecturer.notifyDataSetChanged()
                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

    }
}

