package com.swaralink.external.fragments
// File: <UpdatePeripheralFirmwareFragment.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.swaralinkble.BleManager
import com.android.swaralinkble.showToast
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.databinding.FragmentUpdatePeripheralFirmwareBinding
import com.swaralink.external.getRealPathFromUri
import java.io.File

class UpdatePeripheralFirmwareFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePeripheralFirmwareBinding
    private var filePath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdatePeripheralFirmwareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutToolbar.imgToolbarLeft.setOnClickListener { findNavController().navigateUp() }
        binding.layoutToolbar.tvToolbarTitle.text = "Select Firmware"

        binding.btnSelectFile.setOnClickListener {
            openFileChooser()
        }
        binding.btnInitiateUpdate.setOnClickListener {
            if (filePath.isNotEmpty()) {
                val state = BleManager.getInstance().updatePeripheralFirmware(filePath)
                (requireActivity() as HomeActivity).updateLogState(state)
            } else showToast(requireContext(), "Please select a file.")
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                filePath = getRealPathFromUri(requireContext(), data?.data).toString()
                val file = File(filePath)
                binding.tvFileName.text = file.name
            }
        }

}