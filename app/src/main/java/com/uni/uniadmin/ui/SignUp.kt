package com.uni.uniadmin.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.fragment.app.Fragment


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uni.uniadmin.R
import com.uni.uniadmin.databinding.ActivitySignUpBinding
import com.uni.uniadmin.ui.fragment.signUp.FragmentSignUpMainData

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var grade: String
    private lateinit var section: String


    private lateinit var dep: String
    private lateinit var userImageUri: Uri


    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }



     fun nextFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {

            setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exist_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exist_left_to_right
            )

            replace(
                R.id.fragment_container,
                fragment
            ).commit()
        }
    }     fun previousFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {

            setCustomAnimations(
                R.anim.enter_left_to_right,
                R.anim.exist_left_to_right,
                R.anim.enter_right_to_left,
                R.anim.exist_right_to_left
            )

            replace(
                R.id.fragment_container,
                fragment
            ).commit()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nextFragment(FragmentSignUpMainData())


        grade = ""
        dep = ""
        section = ""
        userImageUri = Uri.EMPTY
        //----------------
        auth = Firebase.auth
       }}