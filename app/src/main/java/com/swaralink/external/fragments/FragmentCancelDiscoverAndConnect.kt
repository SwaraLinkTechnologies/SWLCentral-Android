package com.swaralink.external.fragments
// File: <FragmentCancelDiscoverAndConnect.kt>
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
import com.android.swaralinkble.ConstantUtils
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.databinding.FragmentCancelDiscoverConnectBinding

class FragmentCancelDiscoverAndConnect : Fragment() {
    private lateinit var binding: FragmentCancelDiscoverConnectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCancelDiscoverConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutToolbar.imgToolbarLeft.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutToolbar.tvToolbarTitle.text = "Cancel Connect"

        binding.btnCancel.setOnClickListener {
            val state = BleManager.getInstance().cancelConnect()
            (requireActivity() as HomeActivity).updateLogState(state)
            if (state == ConstantUtils.STATE_SUCCESS) (requireActivity() as HomeActivity).updateCentralState(
                ConstantUtils.STATE_IDLE
            )
        }

    }


}