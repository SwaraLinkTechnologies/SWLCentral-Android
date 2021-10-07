package com.swaralink.external.fragments
// File: <DiscoverAndConnectFragment.kt>
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
import com.swaralink.external.R
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.adapter.KnownDeviceAdapter
import com.swaralink.external.databinding.FragmentDiscoverConnectBinding

class DiscoverAndConnectFragment : Fragment() {
    private lateinit var binding: FragmentDiscoverConnectBinding
    private var KDList = ArrayList<KDData>()
    private lateinit var knownDeviceAdapter: KnownDeviceAdapter
    private var KDData: KDData? = null
    private var logString = StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscoverConnectBinding.inflate(inflater, container, false)
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
        binding.layoutToolbar.tvToolbarTitle.text = "Discover And Connect"


        binding.radioGroupMode.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.radioBtnSpecificDevice -> {
                    binding.group.visibility = View.VISIBLE
                    knownDeviceAdapter.notifyDataSetChanged()
                }
                else -> {
                    binding.group.visibility = View.GONE
                }
            }
        }

        binding.btnBegin.setOnClickListener {

            var timeout = 0

            if (binding.edtTimeout.text.toString().trim().isNotEmpty()) timeout =
                binding.edtTimeout.text.toString().trim().toInt()

            /*logString.clear()
            logString.append("Call : SWLCentral.discoverAndConnect\n")

            if(KDData==null)
            logString.append("connectToFirstDevice: true\n")
            else{
                logString.append("connectToSpecificDevice: true\n")
                logString.append("identityAddress:  ${KDData?.deviceAddress}")
            }

            (requireActivity() as HomeActivity).updateLog(logString.toString())*/
            val state = BleManager.getInstance().discoverAndConnect(timeout, KDData)
            (requireActivity() as HomeActivity).updateLogState(state)

        }
    }


}