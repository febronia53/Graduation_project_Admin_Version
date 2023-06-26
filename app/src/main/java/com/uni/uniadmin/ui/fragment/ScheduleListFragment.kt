package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.Courses
import com.uni.uniadmin.classes.Lecture
import com.uni.uniadmin.classes.ScheduleDataType
import com.uni.uniadmin.classes.Section
import com.uni.uniadmin.data.PassData
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.FragmentScheduleListBinding
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.adapters.ScheduleAdapter
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ScheduleListFragment : Fragment(), PassData {

    private  lateinit var binding: FragmentScheduleListBinding
    private val viewModel: FirebaseViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private   lateinit var progress: ProgressBar
    private   lateinit var currentUser: UserAdmin

    private  lateinit var coursesList: MutableList<Courses>

    private  lateinit var adapter: ScheduleAdapter
    private lateinit var scheduleDataType: MutableList<ScheduleDataType>
    private lateinit var section: String
    private lateinit var department: String
    private lateinit var bottomSheetFragment: BottomSheetFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleListBinding.inflate(layoutInflater)
        section = ""
        department = ""
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


        progress = binding.progressSchedule



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


        binding.scheduleFiltersBtn.setOnClickListener { showBottomSheetSettings() }

        return binding.root
    }

    private fun showBottomSheetSettings() {
        bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        bottomSheetFragment.isCancelable = true
        bottomSheetFragment.show(childFragmentManager, BottomSheetFragment.sheet_schedule_TAG)
    }

    private fun observeDeletedSection() {
        lifecycleScope.launchWhenCreated {
            viewModel.deleteSection.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        progress.visibility = View.GONE

                        Toast.makeText(context, "section deleted successfully", Toast.LENGTH_SHORT)
                            .show()

                    }

                    is Resource.Failure -> {
                        progress.visibility = View.GONE
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
                        progress.visibility = View.GONE

                        Toast.makeText(context, "Lecture deleted successfully", Toast.LENGTH_SHORT)
                            .show()

                    }

                    is Resource.Failure -> {
                        progress.visibility = View.GONE
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
                        coursesList.clear()
                        state.result.forEach {
                            coursesList.add(it)
                        }
                        viewModel.getSection(coursesList, dep, section)
                        viewModel.getLecture(coursesList, dep)
                        progress.visibility = View.VISIBLE
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//
                        delay(300)
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//

                        progress.visibility = View.GONE
                        updateData()



                    }

                    is Resource.Failure -> {
                        progress.visibility = View.GONE
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
                        progress.visibility = View.GONE

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


                    }

                    is Resource.Failure -> {
                        progress.visibility = View.GONE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }
        }

    }

fun updateData(){
    scheduleDataType.clear()
    lifecycleScope.launchWhenCreated {
        observeLectures()
        observeSections()
        delay(200)
    }
    adapter.update(scheduleDataType)
}
    private fun observeLectures() {
        lifecycleScope.launchWhenCreated {
            viewModel.getLecture.collectLatest { state ->

                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        progress.visibility = View.GONE
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

                    }

                    is Resource.Failure -> {
                        progress.visibility = View.GONE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onDataPassed(department: String, section: String, studentId: String) {
        this.department = department
        this.section = section
        if (section.isNotEmpty() && department.isNotEmpty()) {
            viewModel.getCoursesByGrade(currentUser.grade)
            observeCourses(section, department)
        }
    }
}
