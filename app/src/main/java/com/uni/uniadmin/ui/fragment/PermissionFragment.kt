package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.uni.uniadmin.R
import com.uni.uniadmin.adapters.StudentAdapter
import com.uni.uniadmin.classes.PermissionItem
import com.uni.uniadmin.classes.user.UserStudent
import com.uni.uniadmin.data.PassData
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.FragmentPermissionBinding
import com.uni.uniadmin.ui.HomeScreen
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PermissionFragment : Fragment(), PassData {
    private val viewModelAuth: AuthViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    private lateinit var studentsList: MutableList<UserStudent>
    private lateinit var redMessage: TextView
    private val viewModel: FirebaseViewModel by viewModels()
    private lateinit var recyAdapter: StudentAdapter
    lateinit var database: FirebaseFirestore
    private lateinit var binding: FragmentPermissionBinding
    private lateinit var bottomSheetFragment: BottomSheetFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPermissionBinding.inflate(layoutInflater)
        database = FirebaseFirestore.getInstance()

        /* ------------------------------------------------------------*/
        val recyclerView = binding.permissionSearchRecy
        redMessage = binding.messageIndecation
        val permissionText = binding.permissionMessage
        studentsList = arrayListOf()
        currentUser = UserAdmin()
        val bundle = Bundle()
        val permissionFragment = ViewPermissionsFragment()
        /*--------------------------------------------------------------*/
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





        recyAdapter = StudentAdapter(requireContext(), studentsList,
            removePerm = { _, _ ->

            },
            itemClick = { _, item ->

                bundle.putString("userID", item.userId)
                permissionFragment.arguments = bundle
                (activity as HomeScreen).replaceFragment(permissionFragment)

            },
            addPerm = { _, item ->
                val permission = permissionText.text.toString()
                if (permission.isNotEmpty()) {
                    viewModel.addPermission(
                        currentUser.grade,
                        PermissionItem(
                            permission,
                            item.userId,
                            "",
                            item.name,
                            item.grade,
                            item.section,
                            item.department
                        )
                    )
                    observePermission()
                } else {
                    Toast.makeText(context, "you have to write permission text", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyAdapter

        binding.permissionFiltersBtn.setOnClickListener { showBottomSheetSettings() }
        /******/
        binding.viewPermissions.setOnClickListener {
            bundle.putString("userID", "All")
            permissionFragment.arguments = bundle
            (activity as HomeScreen).replaceFragment(permissionFragment)
        }
        showAllPermission()
        /*****/


        return binding.root
    }


    private fun showBottomSheetSettings() {

        bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        bottomSheetFragment.isCancelable = true
        bottomSheetFragment.show(childFragmentManager, BottomSheetFragment.TAG)

    }

    private fun showAllPermission() {
        viewModel.searchStudentAll(currentUser.grade)
        observeStudents()
    }

    private fun observePermission() {
        lifecycleScope.launchWhenCreated {
            viewModel.addPermission.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Toast.makeText(context, "permission added successfully", Toast.LENGTH_SHORT)
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

    private fun observeStudents() {
        lifecycleScope.launchWhenCreated {
            viewModel.searchStudentAll.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        studentsList.clear()

                        it.result.forEach { student ->
                            studentsList.add(student)
                        }
                        if (studentsList.isEmpty()) {
                            redMessage.text =
                                "there in no result to this query make sure the data are correct"
                        }
                        recyAdapter.update(studentsList)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

    }

    private fun observeStudent() {
        lifecycleScope.launchWhenCreated {
            viewModel.searchStudentByID.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        studentsList.clear()
                        studentsList.add(it.result)
                        if (studentsList.isEmpty()) {
                            redMessage.text =
                                "there in no result to this query make sure the data are correct"
                        }
                        recyAdapter.update(studentsList)

                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

    }

    override fun onDataPassed(department: String, section: String, studentId: String) {
        if (section != "any section" && department != "any department") {

            viewModel.searchStudentBySection(currentUser.grade, department, section)
            observeStudents()
        } else if (studentId.isNotEmpty()) {
            viewModel.searchStudentByID(currentUser.grade, studentId)
            observeStudent()
        } else if (department != "any department") {

            viewModel.searchStudentByDepartment(currentUser.grade, department)
            observeStudents()
        } else {
            showAllPermission()
        }

    }

}