package com.swaralink.external.fragments
// File: <SendDataFragment.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.swaralinkble.BleManager
import com.swaralink.external.R
import com.swaralink.external.activity.HomeActivity
import com.swaralink.external.databinding.FragmentSendDataBinding
import com.swaralink.external.getRealPathFromUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Paths

class SendDataFragment : Fragment() {
    private lateinit var binding: FragmentSendDataBinding
    private var isAck = false
    private var logString = StringBuilder()
    private var filePath = ""
    val fileData = StringBuilder()

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.btnSendData.isEnabled = binding.edtMajorHeader.text.toString().trim()
                .isNotEmpty() && binding.edtMinorHeader.text.toString().trim()
                .isNotEmpty() && binding.edtData.text.toString().trim()
                .isNotEmpty() && binding.edtMajorHeader.text.toString()
                .trim().length == 2 && binding.edtMinorHeader.text.toString().trim().length == 2

        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutToolbar.imgToolbarLeft.setOnClickListener { findNavController().navigateUp() }
        binding.layoutToolbar.tvToolbarTitle.text = "Send Data"


        binding.edtMajorHeader.addTextChangedListener(textWatcher)
        binding.edtMinorHeader.addTextChangedListener(textWatcher)
        binding.edtData.addTextChangedListener(textWatcher)

        binding.radioGroupAck.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btnAckYes -> isAck = true
                R.id.btnAckNo -> isAck = false
            }
        }

        binding.btnSendData.setOnClickListener {
            val ID_HDR_MAJOR = binding.edtMajorHeader.text.toString()
            val ID_HDR_MINOR = binding.edtMinorHeader.text.toString()
            val payload = binding.edtData.text.toString().ifEmpty {
                fileData.toString()
            }
            //val payload = edtData.text.toString()

            /*logString.clear()
            logString.append("Call : SWLCentral.sendData\n")
            logString.append("Major ID Header: $ID_HDR_MAJOR\n")
            logString.append("Minor ID Header: $ID_HDR_MINOR\n")
            logString.append("Acknowledge : $isAck\n")
            logString.append("Payload: $payload")

            (requireActivity() as HomeActivity).updateLog(logString.toString())*/

            val state = BleManager.getInstance().sendData(
                ID_HDR_MAJOR, ID_HDR_MINOR, payload, isAck
            )

            (requireActivity() as HomeActivity).updateLogState(state)


            binding.btnSelectFile.setOnClickListener {
                openFileChooser()
            }

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
                //val file = File(filePath)
                //btnSelectFile.text = file.name
                val fileByteArray = Files.readAllBytes(Paths.get(filePath))

                Log.d("SendData", "File payload: ${fileData.toString()}")


                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Default) {
                        fileByteArray.forEachIndexed { index, byte ->
                            fileData.append(Integer.toBinaryString(byte.toInt()))
                        }
                    }

                    withContext(Dispatchers.Main) {
                        binding.btnSendData.performClick()
                    }
                }


            }
        }

}