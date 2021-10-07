package com.swaralink.external.fragments
// File: <RequestDiagnosticLogsFragment.kt>
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
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.databinding.FragmentRequestDiagnosticLogsBinding

class RequestDiagnosticLogsFragment : Fragment() {
    private lateinit var binding: FragmentRequestDiagnosticLogsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestDiagnosticLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutToolbar.imgToolbarLeft.setOnClickListener { findNavController().navigateUp() }

        binding.layoutToolbar.tvToolbarTitle.text = "Request Diagnostic Logs"

        binding.btnRequestDiagnosticLogs.setOnClickListener {
            val state = BleManager.getInstance().requestPeripheralDiagnosticLogs()
            (requireActivity() as HomeActivity).updateLogState(state)
        }

    }
}