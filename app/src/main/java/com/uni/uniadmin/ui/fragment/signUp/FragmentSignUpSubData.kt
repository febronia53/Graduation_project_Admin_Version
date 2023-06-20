package com.uni.uniadmin.ui.fragment.signUp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.android.material.snackbar.Snackbar
import com.uni.uniadmin.R
import com.uni.uniadmin.data.di.SignUpKey
import com.uni.uniadmin.databinding.FragmentSignUpSubDataBinding
import com.uni.uniadmin.ui.SignUp


class FragmentSignUpSubData : Fragment() {
    private lateinit var binding: FragmentSignUpSubDataBinding
    private lateinit var mCollectData: CollectDataListener
    private lateinit var code: String
    private lateinit var userImage: ImageView
    private lateinit var job: String

    private lateinit var grade: String
    private lateinit var mainDataBundle: Bundle
    private lateinit var userImageUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        code=""
        job=""
        grade=""

        binding = FragmentSignUpSubDataBinding.inflate(layoutInflater)

        mainDataBundle = bundleOf()
        userImageUri = Uri.EMPTY
        userImage = binding.signUserImage


//------------------------------------//
        setGradeSpinner()

//------------------------------------//
        parentFragmentManager.setFragmentResultListener(
            SignUpKey.MAIN_DATA, this
        ) { _, result -> mainDataBundle = result }

        binding.chooseImageBtn.setOnClickListener {
            pickImageFromGallery()
        }
        binding.signUpBtn.setOnClickListener {

            code = binding.signCode
                .text.trim().toString()

            job = binding.jobTitleText
                .text.trim().toString()


            if (userImageUri != Uri.EMPTY) {

                if (code.isNotEmpty() && job.isNotEmpty()  && grade.isNotEmpty()) {
                    mainDataBundle.putString("code", code)
                    mainDataBundle.putString("jobTitel", job)
                    mainDataBundle.putString("grade", grade)
                    mainDataBundle.putString("userImageUri", userImageUri.toString())
                    /// ----------- ///
                    mCollectData.signUp(mainDataBundle)
                    /// ------------- ///

                } else {
                    showTopSnackBar(binding.root,  R.string.alldata)

                }
            } else {
                showTopSnackBar(binding.root, R.string.notPickImage)

            }
        }

        binding.backBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(SignUpKey.BACK_DATA, mainDataBundle)
            (activity as SignUp).previousFragment(FragmentSignUpMainData())
        }

        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CollectDataListener) {
            mCollectData = context
        } else {
            Toast.makeText(requireContext(), "error in collect data listener", Toast.LENGTH_SHORT).show()
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SignUp.IMAGE_REQUEST_CODE)
    }

    // To send all data to signUp activity
    public interface CollectDataListener {
        fun signUp(bundle: Bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUp.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            userImageUri = data?.data!!
            userImage.setImageURI(userImageUri)

        }
    }

    private fun setGradeSpinner() {

        val gradeList = resources.getStringArray(R.array.grades)
        val adapter: ArrayAdapter<CharSequence> = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.grades,
                R.layout.spinner_item
            )
        } as ArrayAdapter<CharSequence>
        val gradeSpinner = binding.gradeSpinner
        gradeSpinner.adapter = adapter

        gradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                grade = gradeList[p2]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun showTopSnackBar(view: View, message: Int) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

        val slideInAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide_in_top)
        val slideOutAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide_out_bottom)

        snackBar.view.animation = slideInAnimation
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                snackBar.view.animation = slideOutAnimation
            }
        })


        val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        //  params.setMargins(10,10,10,10)
        snackBar.view.layoutParams = params
        snackBar.show()
    }
}