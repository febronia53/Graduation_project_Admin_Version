package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.uni.uniadmin.R
import com.uni.uniadmin.ui.HomeScreen
import com.uni.uniadmin.ui.fragment.addData.AddCourseFragment
import com.uni.uniadmin.ui.fragment.addData.AddPostFragment
import com.uni.uniadmin.ui.fragment.addData.AddScheduleFragment

class OptionsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_options, container, false)
        val course = view.findViewById<Button>(R.id.add_course)
        val permission = view.findViewById<Button>(R.id.add_permission)
        val sch = view.findViewById<Button>(R.id.add_schedule)
        val post = view.findViewById<Button>(R.id.add_post)
        course.setOnClickListener {
            val addCourse = AddCourseFragment()
            (activity as HomeScreen).replaceFragment(addCourse)
        }
        permission.setOnClickListener {
            val permissionsFragment = PermissionFragment()
            (activity as HomeScreen).replaceFragment(permissionsFragment)
        }
        post.setOnClickListener {
            val addPost = AddPostFragment()
            (activity as HomeScreen).replaceFragment(addPost)
        }
        sch.setOnClickListener {
            val addSch = AddScheduleFragment()
            (activity as HomeScreen).replaceFragment(addSch)
        }
        return view
    }


}