package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniadmin.R
import com.uni.uniadmin.adapters.PermissionAdapter
import com.uni.uniadmin.classes.PermissionItem
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest




@AndroidEntryPoint
class ViewPermissionsFragment : Fragment() {
    private val viewModelAuth : AuthViewModel by viewModels()
    private lateinit var currentUser: UserAdmin
    lateinit var  adapter : PermissionAdapter
    private lateinit var permissionList:MutableList<PermissionItem>
    private val viewModel : FirebaseViewModel by viewModels()


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
        val view= inflater.inflate(R.layout.fragment_view_permissions, container, false)
        permissionList= arrayListOf()
        val recyclerView = view.findViewById<RecyclerView>(R.id.perm_recy_view)
viewModel.getPermission(currentUser.grade)
        observePermissionList()

        adapter= PermissionAdapter(requireContext(),permissionList,

            delete = {pos, item->

                viewModel.deletePermission(currentUser.grade, item.permissionId)
                observePermission()
            })

        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter=adapter


        return view
    }

    private fun observePermissionList() {
        lifecycleScope.launchWhenCreated {
            viewModel.getPermission.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        permissionList.clear()
                        it.result.forEach {permission->
                            permissionList.add(permission)
                        }
                        adapter.update(permissionList)
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context,it.exception,Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }

    }


    private fun observePermission() {
        lifecycleScope.launchWhenCreated {
            viewModel.addPermission.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Toast.makeText(context,it.result,Toast.LENGTH_SHORT).show()

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