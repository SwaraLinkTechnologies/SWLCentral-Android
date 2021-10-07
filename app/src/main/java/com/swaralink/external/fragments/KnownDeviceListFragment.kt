package com.swaralink.external.fragments
// File: <KnownDeviceListFragment.kt>
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
import com.android.swaralinkble.KDData
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.adapter.KnownDeviceAdapter
import com.swaralink.external.databinding.FragmentKnownDeviceListBinding

class KnownDeviceListFragment : Fragment() {
    private lateinit var binding: FragmentKnownDeviceListBinding
    private var kdList = ArrayList<KDData>()
    private lateinit var knownDeviceAdapter: KnownDeviceAdapter
    private var kdData: KDData? = null
    private var position: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentKnownDeviceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutToolbar.imgToolbarLeft.setOnClickListener {
            findNavController().navigateUp()
        }

        kdList.addAll(BleManager.getInstance().getKnownDeviceList())
        knownDeviceAdapter = KnownDeviceAdapter(requireContext(), kdList) {
            position = it
            kdData = kdList[it]
        }

        binding.rvKnownDevices.adapter = knownDeviceAdapter
        binding.layoutToolbar.tvToolbarTitle.text = "Remove From Known Device"

        binding.btnRemoveDevice.setOnClickListener {

            val state = BleManager.getInstance().removeFromKnownDeviceList(kdData)
            if (state == ConstantUtils.STATE_SUCCESS) {
                kdList.removeAt(position!!)
                knownDeviceAdapter.notifyItemRemoved(position!!)
                knownDeviceAdapter.notifyItemRangeChanged(position!!, kdList.size)
            }
            (requireActivity() as HomeActivity).updateLogState(state)
        }
    }
}