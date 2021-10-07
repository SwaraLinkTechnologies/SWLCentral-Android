package com.swaralink.external.fragments
// File: <FragmentClearKDL.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.swaralinkble.BleManager
import com.swaralink.external.databinding.FragmentClearKdlBinding

class FragmentClearKDL : Fragment() {
    private lateinit var binding: FragmentClearKdlBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentClearKdlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutToolbar.imgToolbarLeft.setOnClickListener { findNavController().navigateUp() }

        binding.layoutToolbar.tvToolbarTitle.text = "Clear Known Device List"

        binding.btnClear.setOnClickListener {
            BleManager.getInstance().clearKnownDeviceList()
        }
    }
}