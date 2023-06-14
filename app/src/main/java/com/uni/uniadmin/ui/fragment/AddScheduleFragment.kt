package com.uni.uniadmin.ui.fragment

import android.net.Uri
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
import com.uni.uniadmin.classes.Lecture
import com.uni.uniadmin.classes.Section
import com.uni.uniadmin.classes.user.UserStudent
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.FragmentAddScheduleBinding
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FireStorageViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddScheduleFragment : Fragment() {
lateinit var binding:   FragmentAddScheduleBinding
    private val viewModelAuth : AuthViewModel by viewModels()
    private lateinit var  adapterCourses: ArrayAdapter<String>
    private lateinit var  adapterTeaching: ArrayAdapter<String>
    private val viewModel : FirebaseViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    private lateinit var department: String
    private lateinit var section: String
    private lateinit var course: String
    private lateinit var assistantList: MutableList<Assistant>
    private lateinit var coursesList: MutableList<String>
    private lateinit var teachingList: MutableList<String>


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
        coursesList= arrayListOf()
        assistantList= arrayListOf()
        teachingList= arrayListOf()
        var index =0
        binding = FragmentAddScheduleBinding.inflate(layoutInflater)
        coursesList= arrayListOf()
        teachingList= arrayListOf()

        section=""
        course=""
        department=""
        var teaching=""
        var day=""
var key =""
        binding.radioGroup.setOnCheckedChangeListener { group, id ->
            when(id){

                R.id.radioSection ->{
                    viewModel.getAllAssistant()
                observeAssistant()
                    key="section"
                }
                R.id.radioLecture ->{
                    binding.sectionSpinnerSchedule.visibility=View.INVISIBLE
                    binding.sectionText.visibility=View.INVISIBLE
                    viewModel.getAllProfessor()
                    observeProfessors()
                    key="lecture"
                }

            }
        }

binding.addSchedule.setOnClickListener {
   val from = binding.fromSchedule.text.toString()
    val to = binding.toSchedule.text.toString()
    val place=binding.placeSchedule.text.toString()
    if (key=="lecture"){
    if (department.isNotEmpty()&& from.isNotEmpty()&&to.isNotEmpty()&&course.isNotEmpty()&&teaching.isNotEmpty()&&place.isNotEmpty()){
        viewModel.addLecture(Lecture("",course,course,place,teaching,day,from,to,false),department)
        observeAddedLecture()
    }
    }else{
        if (department.isNotEmpty()&&section.isNotEmpty()&& from.isNotEmpty()&&to.isNotEmpty()&&course.isNotEmpty()&&teaching.isNotEmpty()){
viewModel.addSection(Section("",course,course,place,assistantList[index].name,assistantList[index].code,section,day,from,to,false),department)
            observeAddedSection()
        }
    }
}
        val departmentList = resources.getStringArray(R.array.departement)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.departement,R.layout.spinner_item)
        val autoCom = binding.departementSpinnerSchedule
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                department =departmentList[p2]
                binding.departmentText.text=department}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        val dayList = resources.getStringArray(R.array.Day)
        val adapterDay: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.Day,R.layout.spinner_item)
        val autoComDay = binding.daySpinnerSchedule
        autoComDay.adapter = adapterDay

        autoComDay.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                day =dayList[p2]
                binding.dayText.text=day}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        val sectionList = resources.getStringArray(R.array.Section)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.Section,R.layout.spinner_item)
        val autoCom2 = binding.sectionSpinnerSchedule
        autoCom2.adapter = adapter2

        autoCom2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                section =sectionList[p2]
                binding.sectionText.text=section}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        adapterCourses= ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, coursesList
        )

        val autoCom3 = binding.coursesSpinnerSchedule
        autoCom3.adapter = adapterCourses

        autoCom3.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                course =coursesList[p2]
                binding.courseText.text=course
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        adapterTeaching= ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, teachingList
        )
        val autoComTeaching = binding.teachingSpinnerSchedule
        autoComTeaching.adapter = adapterTeaching

        autoComTeaching.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                teaching =teachingList[p2]
                index=p2
                binding.teachingText.text=teaching
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.getCoursesByGrade(currentUser.grade)
        observeCourses()

        return  binding.root
    }

    private fun observeAddedLecture() {
        lifecycleScope.launchWhenCreated {
            viewModel.addLecture.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Toast.makeText(context,"lecture added successfully",Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
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
                        Toast.makeText(context,"section added successfully",Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
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
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
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
                            teachingList.add(it.name)
                            assistantList.add(it)
                        }
                        adapterTeaching.notifyDataSetChanged()
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
                        adapterCourses.notifyDataSetChanged()
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