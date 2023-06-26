package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uni.uniadmin.R
import com.uni.uniadmin.data.PassData
import com.uni.uniadmin.databinding.BottomSheetLayoutBinding

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetLayoutBinding
    private lateinit var department: String
    private lateinit var section: String
    private lateinit var passDataListener: PassData


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = BottomSheetLayoutBinding.inflate(layoutInflater)
        passDataListener = requireParentFragment() as PassData


        if (this.tag == sheet_schedule_TAG) {
            binding.permissionStudentID.visibility = View.GONE
            binding.switchToFilterById.visibility = View.GONE
        }
        department = "any department"    //default
        section = "any section"      //default

        val search = binding.searchPermission
        val studentID = binding.permissionStudentID

        /******************************************************************************************/
        val departmentList = resources.getStringArray(R.array.department)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.department,
            R.layout.spinner_item
        )
        val autoCom = binding.departmentSpinnerPermission
        autoCom.adapter = adapter
        autoCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                department = departmentList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        /******************************************************************************************/
        val sectionList = resources.getStringArray(R.array.Section)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Section,
            R.layout.spinner_item
        )

        val autoCom2 = binding.sectionSpinnerPermission
        autoCom2.adapter = adapter2
        autoCom2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                section = sectionList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        search.setOnClickListener {
            passDataToParentFragment(
                department,
                section,
                studentID.text.toString()
            )

            dismiss()
        }
        /******************************************************************************************/


        binding.switchToFilterById.setOnCheckedChangeListener { _, isChecked ->
            // Handle the switch state change

            if (isChecked) {
                binding.sectionSpinnerPermission.visibility = View.GONE
                binding.departmentSpinnerPermission.visibility = View.GONE
                binding.sectionPermissionText.visibility = View.GONE
                binding.departmentPermissionText.visibility = View.GONE
                binding.permissionStudentID.isEnabled = true
                binding.permissionStudentID.alpha = 1f

                context?.getColor(R.color.black_100)
                    ?.let { binding.switchToFilterById.setTextColor(it) }

            } else {
                binding.sectionSpinnerPermission.visibility = View.VISIBLE
                binding.departmentSpinnerPermission.visibility = View.VISIBLE
                binding.sectionPermissionText.visibility = View.VISIBLE
                binding.departmentPermissionText.visibility = View.VISIBLE
                binding.permissionStudentID.isEnabled = false
                binding.permissionStudentID.alpha = .5f
                context?.getColor(R.color.black_50)
                    ?.let { binding.switchToFilterById.setTextColor(it) }
            }

        }


        return binding.root
    }

    private fun passDataToParentFragment(department: String, section: String, studentId: String) {
        if (passDataListener != null) {
            passDataListener.onDataPassed(department, section, studentId)
        } else {
            Toast.makeText(context, "null", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        const val TAG = "MyBottomSheetTag"
        const val sheet_schedule_TAG = "sheetScheduleTag"

    }


}