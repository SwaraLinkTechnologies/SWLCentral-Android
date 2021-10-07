package com.swaralink.external.fragments
// File: <MainFragment.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swaralink.external.R
import com.swaralink.external.activity.AboutActivity
import com.swaralink.external.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDiscoverAndConnect.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_discoverAndConnectFragment)
        }

        binding.btnDirectConnect.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_directConnectFragment)
        }

        binding.btnCancelConnect.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_fragmentCancelDiscoverAndConnect)
        }

        binding.btnTerminateConnection.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_fragmentTerminateConnection)
        }

        binding.btnSendData.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_sendDataFragment)
        }

        binding.btnAbout.setOnClickListener {
            startActivity(Intent(context, AboutActivity::class.java))
        }


        binding.btnClearKDl.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_fragmentClearKDL)
        }

        binding.btnGetKDl.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_fragmentGetKnownDeviceList)
        }

        binding.btnRemoveFromKDl.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_knownDeviceListFragment)
        }

        binding.btnUpdatePeripheralFirmware.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_updatePeripheralFirmwareFragment)
        }


        binding.btnRequestDiagnosticLogs.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_requestDiagnosticLogsFragment)
        }

        binding.btnSetPeripheralPriorities.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_setPeripheralPrioritiesFragment)
        }

        binding.btnGetPeripheralPriorities.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_getPeripheralPrioritiesFragment)
        }

        binding.btnGetCurentState.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_getCurrentStateFragment)
        }

        binding.btnSetConfigurationProfile.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_setConfigProfileFragment)
        }


    }


}