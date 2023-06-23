package com.uni.uniadmin.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uni.uniadmin.R
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.ActivityHomeScreenBinding
import com.uni.uniadmin.ui.fragment.*
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FireStorageViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeScreen : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    private val storageViewModel: FireStorageViewModel by viewModels()

    // TODO save the image in a shared prefrance
    lateinit var currentUser: UserAdmin

    private lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.notification -> replaceFragment(OptionsFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.schedule_and_attendees -> {
                    replaceFragment(ScheduleListFragment())
                    updateUser(currentUser)
                }

                R.id.students_permissions -> {
                    replaceFragment(PermissionFragment())
                }

                else -> {
                }
            }
            true
        }

    }


    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateUser(user: UserAdmin) {
        viewModel.getUserStudent(user.userId)
    }

    private fun observeImage() {

        lifecycleScope.launchWhenCreated {
            storageViewModel.getUri.collectLatest { uri ->

                when (uri) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        binding.progressBarImage.visibility = View.GONE
                        Glide.with(this@HomeScreen)
                            .load(uri.result)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)  //TODO https://stackoverflow.com/questions/53140975/load-already-fetched-image-when-offline-in-glide-for-android
                            .placeholder(R.drawable.user_image)
                            .into(binding.userImage)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this@HomeScreen, uri.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                    }
                }
            }


        }
    }


    private fun observeUser() {
        lifecycleScope.launchWhenCreated {
            viewModel.userStudent.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val user = state.result
                        if (user != null) {

                            viewModel.setSession(state.result)
                            binding.userGrade.text = user.grade
                            binding.userDepartment.text = user.jobTitle
                            binding.userName.text = user.name

                        }
                    }

                    is Resource.Failure -> {
                        Toast.makeText(
                            this@HomeScreen,
                            state.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSessionStudent { user ->
            if (user != null) {
                settingsOnStartApp()
                updateUser(user)
                currentUser = user
                if (checkForInternet(this)) {
                    storageViewModel.getUri(user.userId)
                }
                observeUser()
                observeImage()
                replaceFragment(HomeFragment())
            } else {
                Toast.makeText(this, "no user found. have to register", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignUp::class.java))
            }
        }
    }

    private fun settingsOnStartApp() {
        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.notification -> replaceFragment(OptionsFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.schedule_and_attendees -> {
                    replaceFragment(ScheduleListFragment())
                    updateUser(currentUser)
                }

                R.id.students_permissions -> {
                    replaceFragment(PermissionFragment())
                }

                else -> {
                }
            }
            true
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
