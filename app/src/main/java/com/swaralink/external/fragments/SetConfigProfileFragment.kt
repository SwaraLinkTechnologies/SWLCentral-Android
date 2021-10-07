package com.swaralink.external.fragments
// File: <SetConfigProfileFragment.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.swaralink.external.databinding.FragmentSetConfigProfileBinding
import com.swaralink.external.getRealPathFromUri
import java.io.File


class SetConfigProfileFragment : Fragment() {
    private lateinit var binding: FragmentSetConfigProfileBinding
    private var configFile: File? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetConfigProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.imgToolbarLeft.setOnClickListener { findNavController().navigateUp() }
        binding.toolbar.tvToolbarTitle.text = "Set Configuration Profile"

        binding.btnSelectConfigFile.setOnClickListener {
            openFileChooser()
        }

        binding.btnSetConfigProfile.setOnClickListener {
            if (configFile != null) {

                val state = BleManager.getInstance().setConfigurationProfile(configFile!!)
                (requireActivity() as HomeActivity).updateLogState(state)

                binding.tvSelectedFile.text = ""
                configFile = null
            } else {
                showToast(requireContext(), "Please select the configuration file.")
            }
        }


        /*btnSelectJsonFile.setOnClickListener {
            if (configFile != null) {
                Log.d("Json Filepath", configFile!!.absolutePath)
                val jsonData = Files.readAllBytes(configFile!!.toPath())

                try {

                    val encryptedByteArray = ChaCha20Poly1305().encrypt(
                        pText = jsonData
                    )

                    Log.d("encrypted Bytes", encryptedByteArray.contentToString())
                    byteToFile(encryptedByteArray, "som_test_config.swlprofile")

                    *//*val state = BleManager.getInstance().setConfigurationProfile()
                    (requireActivity() as HomeActivity).updateLogState(state)*//*

                    tvSelectedFile.text = ""
                    configFile = null
                } catch (e: Exception) {
                    Log.e("Decryption Error", e.message.toString())
                }
            } else {
                showToast(requireContext(), "Please select the configuration file.")
            }
        }*/

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
                val filePath = getRealPathFromUri(requireContext(), data?.data).toString()
                configFile = File(filePath)
                binding.tvSelectedFile.text = configFile?.name
            }
        }
}