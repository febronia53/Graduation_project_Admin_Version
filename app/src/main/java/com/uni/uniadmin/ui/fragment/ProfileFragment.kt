package com.uni.unistudent.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uni.uniadmin.R
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.databinding.FragmentProfileBinding
import com.uni.uniadmin.ui.fragment.BottomSheetFragment
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FireStorageViewModel
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var bottomSheetFragment: BottomSheetFragment
    lateinit var currentUser: UserAdmin
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userImageUri: Uri
    private val storageViewModel: FireStorageViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val fireStorageViewModel: FireStorageViewModel by viewModels()
    private val CODEPICKPROFILEIMAGE = 333
    private val PROFILEFRAGMENT = R.layout.fragment_profile
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        userImageUri = Uri.EMPTY
        //----------------
        auth = Firebase.auth
        //----------------
        authViewModel.getSessionStudent { user ->
            if (user != null) {
                currentUser = user
                binding.userAdmin = currentUser

                if (context?.let { checkForInternet(it) } == true) {
                    storageViewModel.getUri(user.userId)

                }
                observeImage()

            } else {
                Toast.makeText(
                    context,
                    getString(R.string.error_message_in_profile_screen),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.settingBtn.setOnClickListener { showBottomSheetSettings() }
        binding.chooseImageProfileBtn.setOnClickListener {
            pickImageFromGallery()
        }
        if (savedInstanceState != null) {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_profile, container, false
            )
            userImageUri = Uri.parse(savedInstanceState.getString("userImageUri"))
            binding.imageProfile.setImageURI(userImageUri)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("fragment", PROFILEFRAGMENT)
        outState.putString("userImageUri", userImageUri.toString())
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, CODEPICKPROFILEIMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == CODEPICKPROFILEIMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            //@WALID TODO HERE MUST BE BACK TO SAME STATE (PROFILE FRAGMENT) AFTER PICK IMAGE
            userImageUri = data?.data!!
            binding.imageProfile.setImageURI(userImageUri)

            if (userImageUri != Uri.EMPTY) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    fireStorageViewModel.addUri(userId, userImageUri)
                    observeUploadedImage()
                }
            }


        }

    }


    private fun observeUploadedImage() {
        lifecycleScope.launchWhenCreated {
            fireStorageViewModel.addUri.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBarImageProfile.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBarImageProfile.visibility = View.GONE
                    }

                    is Resource.Failure -> {
                        binding.progressBarImageProfile.visibility = View.GONE
                        Toast.makeText(context, it.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }

            }
        }

    }

    private fun showBottomSheetSettings() {
        bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        bottomSheetFragment.isCancelable = true
        bottomSheetFragment.show(childFragmentManager, BottomSheetFragment.TAG)
    }

    private fun observeImage() {

        lifecycleScope.launchWhenCreated {
            storageViewModel.getUri.collectLatest { uri ->

                when (uri) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        binding.progressBarImageProfile.visibility = View.GONE
                        Glide.with(this@ProfileFragment)
                            .load(uri.result)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)  //TODO https://stackoverflow.com/questions/53140975/load-already-fetched-image-when-offline-in-glide-for-android
                            .placeholder(R.drawable.user_image)
                            .into(binding.imageProfile)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(context, uri.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                    }
                }
            }


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

    private fun backToProfile() {
        replaceFragment(ProfileFragment())

    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

    }
}