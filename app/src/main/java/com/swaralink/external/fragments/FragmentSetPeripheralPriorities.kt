package com.swaralink.external.fragments
// File: <FragmentSetPeripheralPriorities.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.swaralinkble.BleManager
import com.android.swaralinkble.ConstantUtils
import com.swaralink.external.R
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.databinding.FragmentSetPeripheralPrioritiesBinding


class FragmentSetPeripheralPriorities : Fragment() {
    private lateinit var binding: FragmentSetPeripheralPrioritiesBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var priority1: String
    private lateinit var priority2: String
    private lateinit var priority3: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetPeripheralPrioritiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutToolbar.imgToolbarLeft.setOnClickListener { findNavController().navigateUp() }
        binding.layoutToolbar.tvToolbarTitle.text = "Set Peripheral Priorities"

        priority1 = sharedPreferences.getString(
            ConstantUtils.PERIPHERAL_PRIORITY_1, ConstantUtils.DEFAULT_PERIPHERAL_PRIORITY
        ).toString()

        priority2 = sharedPreferences.getString(
            ConstantUtils.PERIPHERAL_PRIORITY_2, ConstantUtils.DEFAULT_PERIPHERAL_PRIORITY
        ).toString()

        priority3 = sharedPreferences.getString(
            ConstantUtils.PERIPHERAL_PRIORITY_3, ConstantUtils.DEFAULT_PERIPHERAL_PRIORITY
        ).toString()


        setCurrentPriorities()


        binding.radioGrp1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioBtn11 -> priority1 = "01"
                R.id.radioBtn12 -> priority1 = "02"
                R.id.radioBtn13 -> priority1 = "03"
            }
        }
        binding.radioGrp2.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioBtn21 -> priority2 = "01"
                R.id.radioBtn22 -> priority2 = "02"
                R.id.radioBtn23 -> priority2 = "03"
            }
        }
        binding.radioGrp3.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioBtn31 -> priority3 = "01"
                R.id.radioBtn32 -> priority3 = "02"
                R.id.radioBtn33 -> priority3 = "03"
            }
        }


        binding.btnSetPriority.setOnClickListener {
            val p1 = if (priority1 == "00") "01" else priority1
            val p2 = if (priority2 == "00") "01" else priority2
            val p3 = if (priority3 == "00") "01" else priority3
            val payload = "$p1$p2$p3"

            val state = BleManager.getInstance().setPeripheralPriorities(payload)
            (requireActivity() as HomeActivity).updateLogState(state)
        }
    }

    private fun setCurrentPriorities() {
        when (priority1) {
            "00", "01" -> binding.radioBtn11.isChecked = true
            "02" -> binding.radioBtn12.isChecked = true
            "03" -> binding.radioBtn13.isChecked = true
        }

        when (priority2) {
            "00", "01" -> binding.radioBtn21.isChecked = true
            "02" -> binding.radioBtn22.isChecked = true
            "03" -> binding.radioBtn23.isChecked = true
        }

        when (priority3) {
            "00", "01" -> binding.radioBtn31.isChecked = true
            "02" -> binding.radioBtn32.isChecked = true
            "03" -> binding.radioBtn33.isChecked = true
        }


        editor.putString(
            ConstantUtils.PERIPHERAL_PRIORITY_1, priority1
        )
        editor.putString(
            ConstantUtils.PERIPHERAL_PRIORITY_2, priority2
        )
        editor.putString(
            ConstantUtils.PERIPHERAL_PRIORITY_3, priority3
        )
        editor.commit()
    }

}