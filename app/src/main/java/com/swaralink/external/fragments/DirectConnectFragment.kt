package com.swaralink.external.fragments
// File: <DirectConnectFragment.kt>
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
import com.android.swaralinkble.KDData
import com.android.swaralinkble.showToast
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.adapter.KnownDeviceAdapter
import com.swaralink.external.databinding.FragmentDirectConnectBinding

class DirectConnectFragment : Fragment() {
    private lateinit var binding: FragmentDirectConnectBinding
    private var KDList = ArrayList<KDData>()
    private lateinit var knownDeviceAdapter: KnownDeviceAdapter
    private var KDData: KDData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDirectConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutToolbar.imgToolbarLeft.setOnClickListener {
            findNavController().navigateUp()
        }

        KDList.addAll(BleManager.getInstance().getKnownDeviceList())

        knownDeviceAdapter = KnownDeviceAdapter(requireContext(), KDList) {
            KDData = KDList[it]
        }

        binding.rvKnownDevices.adapter = knownDeviceAdapter
        binding.layoutToolbar.tvToolbarTitle.text = "Direct Connect"

        binding.btnDirectConnect.setOnClickListener {
            if (KDData == null) {
                showToast(requireContext(), "Please select a device.")
                return@setOnClickListener
            }
            var timeout = 0

            if (binding.edtTimeout.text.toString().trim().isNotEmpty()) timeout =
                binding.edtTimeout.text.toString().trim().toInt()


            val state = BleManager.getInstance().directConnect(timeout, KDData)
            (requireActivity() as HomeActivity).updateLogState(state)

        }
    }

}