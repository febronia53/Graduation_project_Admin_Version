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
import com.google.firebase.firestore.FirebaseFirestore
import com.uni.uniadmin.R
import com.uni.uniadmin.adapters.StudentAdapter
import com.uni.uniadmin.classes.Assistant
import com.uni.uniadmin.classes.PermissionItem
import com.uni.uniadmin.classes.user.UserStudent
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.data.di.FireStoreTable
import com.uni.uniadmin.data.di.UserTypes
import com.uni.uniadmin.ui.HomeScreen
import com.uni.uniadmin.ui.fragment.addData.AddCourseFragment
import com.uni.uniadmin.ui.fragment.addData.AddPostFragment
import com.uni.uniadmin.ui.fragment.addData.AddScheduleFragment
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PermissionFragment : Fragment() {
    private val viewModelAuth : AuthViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    private lateinit var studentsList: MutableList<UserStudent>
    private lateinit var department: String
    private lateinit var section: String
    private lateinit var redMessage:TextView
    private val viewModel : FirebaseViewModel by viewModels()
    lateinit var  recyAdapter : StudentAdapter
    lateinit var database:FirebaseFirestore
// TODO() navigate to the view Permission screen
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    database = FirebaseFirestore.getInstance()
    /* ------------------------------------------------------------*/
   // getAllAssistants()
    val bundle=Bundle()
    val permissionFragment=ViewPermissionsFragment()
        studentsList= arrayListOf()
        currentUser= UserAdmin()
        viewModelAuth.getSessionStudent {user->
        if (user != null){
            currentUser = user
        }else
        {
            Toast.makeText(context,"there is an error on loading user data",Toast.LENGTH_SHORT).show()
        }

        }
       val view = inflater.inflate(R.layout.fragment_permission, container, false)


/*--------------------------------------------------------------------------------------------------------*/

    val course=view.findViewById<Button>(R.id.add_c)
    val sch=view.findViewById<Button>(R.id.add_l)
    val post=view.findViewById<Button>(R.id.add_p)
    course.setOnClickListener {
        val addCourse= HomeFragment()
        (activity as HomeScreen).replaceFragment(addCourse)
    }

    post.setOnClickListener {
        val addPost= AddPostFragment()
        (activity as HomeScreen).replaceFragment(addPost)
    }
    sch.setOnClickListener {
        val addSch= ScheduleListFragment()
        (activity as HomeScreen).replaceFragment(addSch)
    }

    /*--------------------------------------------------------------------------------------------*/
        val recyclerView = view.findViewById<RecyclerView>(R.id.permission_search_recy)

        val departmentText = view.findViewById<TextView>(R.id.department_permission_text)
        val sectionText = view.findViewById<TextView>(R.id.section_permission_text)
         redMessage = view.findViewById<TextView>(R.id.message_indecation)

        val  permissionText= view.findViewById<EditText>(R.id.permission_message)
        val  studentID= view.findViewById<EditText>(R.id.permission_student_ID)

        val search = view.findViewById<Button>(R.id.search_permission)
        val viewPermission = view.findViewById<Button>(R.id.view_permissions)
        viewPermission.setOnClickListener {
        bundle.putString("userID","All")
        permissionFragment.arguments=bundle
        (activity as HomeScreen).replaceFragment(permissionFragment)
    }
        department=""
        section=""
        val departmentList = resources.getStringArray(R.array.departement)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.departement,R.layout.spinner_item)
        val autoCom = view.findViewById<Spinner>(R.id.department_spinner_permission)
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                department =departmentList[p2]
                departmentText.text=department}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        val sectionList = resources.getStringArray(R.array.Section)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),R.array.Section,R.layout.spinner_item)
        val autoCom2 = view.findViewById<Spinner>(R.id.section_spinner_permission)
        autoCom2.adapter = adapter2

        autoCom2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                section =sectionList[p2]
                sectionText.text=section}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        search.setOnClickListener {
            val studentID =studentID.text.toString()
// TODO code neeeeed to be changed
            if (section!="any section"&&department!="choose departement")
            {
                viewModel.searchStudentBySection(currentUser.grade,department,section)
                observeStudents()
            }else if(studentID.isNotEmpty())
            {
                viewModel.searchStudentByID(currentUser.grade,studentID)
                observeStudent()
            }else if (department!="any departement")
            {
                viewModel.searchStudentByDepartment(currentUser.grade,department)
                observeStudents()
            }else{
                viewModel.searchStudentAll(currentUser.grade)
                observeStudents()

            }
        }

        recyAdapter= StudentAdapter(requireContext(),studentsList,
            removePerm = {pos, item->

            },
            itemClick = {pos, item->

                bundle.putString("userID",item.userId)
                permissionFragment.arguments=bundle
                (activity as HomeScreen).replaceFragment(permissionFragment)

            },

            addPerm = {pos, item->
                val permission=permissionText.text.toString()
                if (permission.isNotEmpty()){
                viewModel.addPermission(currentUser.grade,
                    PermissionItem(
                        permission,
                        item.userId,
                        "",
                        item.name,
                        item.grade,
                        item.section,
                        item.department)
                )
observePermission()
                }else{
                    Toast.makeText(context,"you have to write permission text",Toast.LENGTH_SHORT).show()
                }
            })

        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter=recyAdapter

        return view
    }

    private fun observePermission() {
        lifecycleScope.launchWhenCreated {
            viewModel.addPermission.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Toast.makeText(context,"permission added successfully",Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }
    }

    private fun observeStudents(){
        lifecycleScope.launchWhenCreated {
            viewModel.searchStudentAll.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        studentsList.clear()

                        it.result.forEach{ student ->
                            studentsList.add(student)
                        }
                        if (studentsList.isEmpty()){
                            redMessage.text="there in no result to this query make sure the data are correct"
                        }
                        recyAdapter.update(studentsList)
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }

    }
    private fun observeStudent(){
        lifecycleScope.launchWhenCreated {
            viewModel.searchStudentByID.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        studentsList.clear()
                            studentsList.add(it.result)
                        if (studentsList.isEmpty()){
                            redMessage.text="there in no result to this query make sure the data are correct"
                        }
                        recyAdapter.update(studentsList)

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }

    }

  /*  fun getAllAssistants( ) {
        val docRef = database.collection(FireStoreTable.userTeaching)
        for (i in 0..5){
            docRef.add(Assistant("name_"+i.toString(),(2000+i).toString(),"assistant" ))
            docRef.add(Assistant("prof_name_"+i.toString(),(5000+i).toString(),"lecturer" ))
        }

    }*/
}