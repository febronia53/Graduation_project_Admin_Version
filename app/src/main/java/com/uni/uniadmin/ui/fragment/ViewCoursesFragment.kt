package com.uni.uniadmin.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uni.uniadmin.R
import com.uni.uniadmin.adapters.CourseAdapter
import com.uni.uniadmin.classes.Courses
import com.uni.uniadmin.classes.Lecture
import com.uni.uniadmin.classes.Section
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.FragmentScheduleListBinding
import com.uni.uniadmin.databinding.FragmentViewCoursesBinding
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.adapters.ScheduleAdapter
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ViewCoursesFragment : Fragment() {
 lateinit var binding:FragmentViewCoursesBinding
    private val viewModel: FirebaseViewModel by viewModels()
var  flage =false
    private val authViewModel: AuthViewModel by viewModels()
    lateinit var currentUser: UserAdmin

    lateinit var adapter:CourseAdapter
lateinit var coursesList:MutableList<Courses>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        coursesList= arrayListOf()
        currentUser= UserAdmin()
        authViewModel.getSessionStudent {user->
            if (user != null){
                currentUser = user
            }else
            {
                Toast.makeText(context,"there is an error on loading user data", Toast.LENGTH_SHORT).show()
            }

        }
        binding = FragmentViewCoursesBinding.inflate(layoutInflater)
        binding.allCourses.setBackgroundColor(Color.GREEN)
binding.allCourses.setOnClickListener {
    flage = !flage
    update()
}
        adapter= CourseAdapter(requireContext(),coursesList,


            deleteCourse = { pos, item ->
             viewModel.deleteCourse(item.courseCode)
                observeDeletedCourse()
                viewModel.getCoursesByGrade(currentUser.grade)

            })

//-------------- setting the recycler data---------------------------//
        binding.recCourses.layoutManager= LinearLayoutManager(requireContext())
        binding.recCourses.adapter=adapter
//-------------- setting the recycler data---------------------------//
   update()
        observeCourse()
   return binding.root }

    private fun observeDeletedCourse() {

        lifecycleScope.launchWhenCreated {
            viewModel.deleteCourse.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Toast.makeText(context,"Course deleted successfully", Toast.LENGTH_LONG).show()

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,state.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                    else->{}
                }
            }
        }}

    private fun observeCourse() {

        lifecycleScope.launchWhenCreated {
            viewModel.getCoursesByGrade.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        coursesList.clear()
                        state.result.forEach {
                            coursesList.add(it)
                        }
                        adapter.update(coursesList)
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,state.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                    else->{}
                }
            }
        }}

fun update(){
    if (flage)
    {
        binding.allCourses.setBackgroundColor(Color.RED)
        viewModel.getAllCourses()
    }else {
        binding.allCourses.setBackgroundColor(Color.GREEN)
        viewModel.getCoursesByGrade(currentUser.grade)
    }
}
}