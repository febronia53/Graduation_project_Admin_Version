package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.Courses
import com.uni.uniadmin.classes.Lecture
import com.uni.uniadmin.classes.ScheduleDataType
import com.uni.uniadmin.classes.Section
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.FragmentAddScheduleBinding
import com.uni.uniadmin.databinding.FragmentScheduleListBinding
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.adapters.ScheduleAdapter
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ScheduleListFragment : Fragment() {

    lateinit var binding: FragmentScheduleListBinding
    private val viewModel: FirebaseViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    lateinit var progress: ProgressBar
    lateinit var currentUser: UserAdmin

    lateinit var coursesList: MutableList<Courses>

    lateinit var adapter: ScheduleAdapter
    lateinit var scheduleDataType: MutableList<ScheduleDataType>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        coursesList = arrayListOf()
        scheduleDataType = arrayListOf()

        currentUser = UserAdmin()
        authViewModel.getSessionStudent { user ->
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

        binding = FragmentScheduleListBinding.inflate(layoutInflater)
        progress = binding.progressSchedule
        var section = ""
        var department = ""
        val departmentList = resources.getStringArray(R.array.departement)
        val adapterDepartment: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.departement,
            R.layout.spinner_item
        )
        val autoCom = binding.departementSpinnerSchedule
        autoCom.adapter = adapterDepartment
        autoCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                department = departmentList[p2]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        val sectionList = resources.getStringArray(R.array.Section)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Section,
            R.layout.spinner_item
        )
        val autoCom2 = binding.sectionSpinnerSchedule
        autoCom2.adapter = adapter2
        autoCom2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                section = sectionList[p2]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        coursesList = arrayListOf()
        scheduleDataType = arrayListOf()

        adapter = ScheduleAdapter(requireContext(), scheduleDataType,

            onItemClicked = { pos, item ->
                Toast.makeText(requireContext(), item.professorName, Toast.LENGTH_SHORT).show()
            },
            onAttendClicked = { pos, item ->
                if (item.type == ScheduleAdapter.VIEW_TYPE_ONE) {
                    if (section.isNotEmpty() && department.isNotEmpty()) {
                        viewModel.deleteSection(
                            Section(
                                item.eventId,
                                item.courseID,
                                item.courseName,
                                item.hallID,
                                "",
                                section,
                                "",
                                "",
                                "",
                                "", false
                            ), department
                        )
                        observeDeletedSection()
                    } else {
                        Toast.makeText(context, "make sure to type all data", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    if (department.isNotEmpty()) {
                        viewModel.deleteLecture(
                            Lecture(item.eventId, item.courseID, "", "", "", "", "", "", false),
                            department
                        )
                        observeDeletedLecture()
                    } else {
                        Toast.makeText(
                            context,
                            "make sure to chose the departement",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

//-------------- setting the recycler data---------------------------//
        binding.recyclerSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSchedule.adapter = adapter
//-------------- setting the recycler data---------------------------//


        binding.searchSchedule.setOnClickListener {
            if (section.isNotEmpty() && department.isNotEmpty()) {
                viewModel.getCoursesByGrade(currentUser.grade)
                observeCourses(section, department)
            } else {
                Toast.makeText(context, "make sure to type all data", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun observeDeletedSection() {
        lifecycleScope.launchWhenCreated {
            viewModel.deleteSection.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        progress.visibility = View.INVISIBLE

                        Toast.makeText(context, "section deleted successfully", Toast.LENGTH_SHORT)
                            .show()

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

    private fun observeDeletedLecture() {
        lifecycleScope.launchWhenCreated {
            viewModel.deleteLecture.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        progress.visibility = View.INVISIBLE

                        Toast.makeText(context, "Lecture deleted successfully", Toast.LENGTH_SHORT)
                            .show()

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

    private fun observeCourses(section: String, dep: String) {
        lifecycleScope.launchWhenCreated {
            viewModel.getCoursesByGrade.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        state.result.forEach {
                            coursesList.add(it)
                        }
                        viewModel.getSection(coursesList, dep, section)
                        viewModel.getLecture(coursesList, dep)
                        progress.visibility = View.VISIBLE
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//
                        delay(200)
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//
                        progress.visibility = View.INVISIBLE

                        observeLectures()
                        observeSections()

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


    private fun observeSections() {
        lifecycleScope.launchWhenCreated {
            viewModel.getSection.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        progress.visibility = View.INVISIBLE

                        state.result.forEach {
                            scheduleDataType.add(
                                ScheduleDataType(
                                    it.sectionId,
                                    it.courseName,
                                    it.courseCode,
                                    it.lapID,
                                    it.assistantName,
                                    it.day,
                                    it.time,
                                    it.endTime,
                                    ScheduleAdapter.VIEW_TYPE_ONE,
                                    it.isRunning
                                )
                            )
                        }
                        adapter.update(scheduleDataType)

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


    private fun observeLectures() {
        lifecycleScope.launchWhenCreated {
            viewModel.getLecture.collectLatest { state ->

                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        progress.visibility = View.INVISIBLE
                        state.result.forEach {
                            scheduleDataType.add(
                                ScheduleDataType(
                                    it.lectureId,
                                    it.courseName,
                                    it.courseCode,
                                    it.hallID,
                                    it.professorName,
                                    it.day,
                                    it.time,
                                    it.endTime,
                                    ScheduleAdapter.VIEW_TYPE_TWO,
                                    it.isRunning

                                )
                            )
                        }
                        adapter.update(scheduleDataType)
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
}
