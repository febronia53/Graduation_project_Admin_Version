package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uni.uniadmin.databinding.FragmentBottomSheetSettingBinding


class BottomSheetSettingFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetSettingBinding.inflate(layoutInflater)

        binding.logOutBtn.setOnClickListener {
            Toast.makeText(context, "Log Out", Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }


    companion object {
        const val TAG = "BottomSheetSetting"
    }


}