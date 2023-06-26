package com.uni.uniadmin.ui.fragment.addData

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.Assistant
import com.uni.uniadmin.classes.Lecture
import com.uni.uniadmin.classes.Section
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.FragmentAddScheduleBinding
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar


@AndroidEntryPoint
class AddScheduleFragment : Fragment() {
    lateinit var binding: FragmentAddScheduleBinding
    private val viewModelAuth: AuthViewModel by viewModels()
    private lateinit var adapterCourses: ArrayAdapter<String>
    private lateinit var adapterTeaching: ArrayAdapter<String>
    private val viewModel: FirebaseViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    private lateinit var department: String
    private lateinit var section: String
    private lateinit var course: String
    private lateinit var assistantList: MutableList<Assistant>
    private lateinit var coursesList: MutableList<String>
    private lateinit var teachingList: MutableList<String>
    private var isTimeAdd = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser = UserAdmin()
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
        coursesList = arrayListOf()
        assistantList = arrayListOf()
        teachingList = arrayListOf()
        var index = 0
        binding = FragmentAddScheduleBinding.inflate(layoutInflater)
        coursesList = arrayListOf()
        teachingList = arrayListOf()

        section = ""
        course = ""
        department = ""
        var teaching = ""
        var day = ""
        var key = ""

        binding.fromSchedule.setOnClickListener {
            showTimePickerDialog(
                binding.fromSchedule,
                binding.amPmFromTime
            )
        }
        binding.toSchedule.setOnClickListener {
            showTimePickerDialog(
                binding.toSchedule,
                binding.amPmToTime
            )
        }

        binding.radioGroup.check(R.id.radioLecture)
        binding.radioGroup.setOnCheckedChangeListener { group, id ->
            when (id) {
                R.id.radioSection -> {
                    binding.sectionSpinnerSchedule.visibility = View.VISIBLE
                    binding.sectionText.visibility = View.VISIBLE
                    binding.addLectureTxt.text = getString(R.string.addSection)
                    viewModel.getAllAssistant()
                    observeAssistant()
                    key = "section"
                }

                R.id.radioLecture -> {
                    binding.sectionSpinnerSchedule.visibility = View.GONE
                    binding.sectionText.visibility = View.GONE
                    binding.addLectureTxt.text = getString(R.string.addLecture)
                    viewModel.getAllProfessor()
                    observeProfessors()
                    key = "lecture"
                }

            }
        }

        binding.addSchedule.setOnClickListener {

            val from = binding.fromSchedule.text.toString()
            val to = binding.toSchedule.text.toString()

            val place = binding.placeSchedule.text.toString()
            if (key == "lecture") {
                if (department.isNotEmpty() && from.isNotEmpty() && to.isNotEmpty() && course.isNotEmpty() && teaching.isNotEmpty() && place.isNotEmpty()) {
                    //TODO() here the name of the course is the same as the code ether place the name or remove it

                    viewModel.addLecture(
                        Lecture(
                            "",
                            course,
                            course,
                            place,
                            teaching,
                            day,
                            from,
                            to,
                            false
                        ), department
                    )
                    observeAddedLecture()
                }
            } else {
                //TODO() here the name of the course is the same as the code ether place the name or remove it

                if (department.isNotEmpty() && section.isNotEmpty() && from.isNotEmpty() && to.isNotEmpty() && course.isNotEmpty() && teaching.isNotEmpty()) {
                    viewModel.addSection(
                        Section(
                            "",
                            course,
                            course,
                            place,
                            assistantList[index].name,
                            assistantList[index].code,
                            section,
                            day,
                            from,
                            to,
                            false
                        ), department
                    )
                    observeAddedSection()
                }
            }
        }
        val departmentList = resources.getStringArray(R.array.department)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.department,
            R.layout.spinner_item
        )

        val autoCom = binding.departementSpinnerSchedule
        autoCom.adapter = adapter
        autoCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                department = departmentList[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        val dayList = resources.getStringArray(R.array.Day)
        val adapterDay: ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(requireContext(), R.array.Day, R.layout.spinner_item)
        val autoComDay = binding.daySpinnerSchedule
        autoComDay.adapter = adapterDay

        autoComDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                day = dayList[p2]

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

        adapterCourses = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item, coursesList
        )

        val autoCom3 = binding.coursesSpinnerSchedule
        autoCom3.adapter = adapterCourses

        autoCom3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                course = coursesList[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        adapterTeaching = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item, teachingList
        )
        val autoComTeaching = binding.teachingSpinnerSchedule
        autoComTeaching.adapter = adapterTeaching

        autoComTeaching.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                teaching = teachingList[p2]
                index = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.getCoursesByGrade(currentUser.grade)
        observeCourses()

        binding.backFragmentBtn.setOnClickListener { finishFragment() }

        return binding.root
    }

    private fun finishFragment() {
        parentFragmentManager.popBackStack()
    }

    private fun showTimePickerDialog(mTime: TextView, amPmTxt: TextView) {
        val currentTime = Calendar.getInstance()
        val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentTime.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                isTimeAdd = true
                val amPm = if (hourOfDay < 12) "AM" else "PM"
                var hour = hourOfDay % 12
                if (hour == 0) {
                    hour = 12
                }
                amPmTxt.text = amPm
                mTime.text = "$hour:$minute"

            }, startHour, startMinute, false
        ).show()
    }

    private fun observeAddedLecture() {
        lifecycleScope.launchWhenCreated {
            viewModel.addLecture.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Toast.makeText(context, "lecture added successfully", Toast.LENGTH_SHORT)
                            .show()

                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observeAddedSection() {
        lifecycleScope.launchWhenCreated {
            viewModel.addSection.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Toast.makeText(context, "section added successfully", Toast.LENGTH_SHORT)
                            .show()

                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observeProfessors() {
        lifecycleScope.launchWhenCreated {
            viewModel.getAllProfessor.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        teachingList.clear()
                        it.result.forEach {
                            teachingList.add(it.name)
                        }
                        adapterTeaching.notifyDataSetChanged()
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
                        teachingList.clear()
                        assistantList.clear()
                        it.result.forEach {
                            Log.e("teachine", it.name)
                            teachingList.add(it.name)
                            assistantList.add(it)
                        }
                        adapterTeaching.notifyDataSetChanged()
                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
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
                        /*  viewModel.getAllAssistant()
                          viewModel.getAllProfessor()
                          delay(200)
                          observeAssistant()*/
                        adapterCourses.notifyDataSetChanged()
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