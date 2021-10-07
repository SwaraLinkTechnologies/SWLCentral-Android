package com.swaralink.external.activity
// File: <HomeActivity.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.swaralinkble.BleManager
import com.android.swaralinkble.ConnectionEventListener
import com.android.swaralinkble.ConstantUtils
import com.android.swaralinkble.showToast
import com.swaralink.external.adapter.LogAdapter
import com.swaralink.external.databinding.ActivityHomeBinding
import com.swaralink.external.removeSpaces
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var logList = ArrayList<String>()
    private lateinit var logAdapter: LogAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val dateFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private var logString = StringBuilder()


    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            evtFunctionCall = {
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append(it)
                logList.add(logString.toString())
                notifyLog()
            }

            evtUpdatedState = { state, deviceData, description ->
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event: SWLCentral.evtUpdateState\n")
                logString.append("State : $state\n")

                if (state == ConstantUtils.STATE_CONNECTED || state == ConstantUtils.STATE_DISCOVERED) {
                    logString.append("deviceName: ${deviceData?.deviceName}\n")
                    logString.append("IdentityAddress: ${deviceData?.identityAddress}\n")
                    logString.append("currentPairedCentralDeviceID: ${deviceData?.assignedPairedDeviceID}\n")

                }

                logString.append("Description: $description\n")
                logList.add(logString.toString())
                notifyLog()
                runOnUiThread { binding.tvSwlCentralStatus.text = state }
            }

            evtUpdatedStateFirmwareUpdate = { state, description ->
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event: SWLCentral.evtUpdatedStateFirmwareUpdate\n")
                logString.append("State : $state\n")
                if (description.isNotEmpty()) logString.append("Description: $description")
                logList.add(logString.toString())
                notifyLog()
            }


            evtConnectionEstablished = {
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event: SWLCentral.evtConnectionEstablished")
                logList.add(logString.toString())
                notifyLog()
            }

            evtConnectionTerminated = {
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtConnectionTerminated")
                logList.add(logString.toString())
                notifyLog()
            }

            evtP2cDataReceived =
                { isAcknowledged, ID_HDR_MAJOR, ID_HDR_MINOR, DATA_PAYLOAD, description ->
                    logString.clear()
                    logString.append("\r------------\n")
                    logString.append(dateFormatter.format(Date()) + "\n")
                    logString.append("Event : SWLCentral.evtP2cDataReceived\n")
                    logString.append("Major ID Header: $ID_HDR_MAJOR\n")
                    logString.append("Minor ID Header: $ID_HDR_MINOR\n")
                    logString.append("Acknowledge: $isAcknowledged\n")
                    logString.append("Payload: ${DATA_PAYLOAD.removeSpaces()}\n")
                    logString.append("Description: $description")

                    logList.add(logString.toString())
                    notifyLog()

                }

            evtP2cInternalDataReceived = { ID_HDR_MAJOR, ID_HDR_MINOR, DATA_PAYLOAD ->

                /*logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtP2cInternalDataReceived\n")
                logString.append("Major ID Header: $ID_HDR_MAJOR\n")
                logString.append("Minor ID Header: $ID_HDR_MINOR\n")
                logString.append("Payload: ${DATA_PAYLOAD.removeSpaces()}")

                logList.add(logString.toString())
                notifyLog()*/
            }


            evtKnownDeviceList = { KDList, description ->
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtKnownDeviceList\n")
                logString.append("listSize: ${KDList.size}\n")
                logString.append("Description: $description")
                KDList.forEachIndexed { index, kdlData ->
                    logString.append("\nDevice$index.deviceName: ${kdlData.deviceName}\n")
                    logString.append("Device$index.BLEidentityAddress: ${kdlData.deviceAddress}\n")
                    if (BleManager.INTERNAL_BUILD) {
                        logString.append("Device$index.assignedPairedDeviceID: ${kdlData.deviceId}\n")
                        logString.append("Device$index.assignedPairingKey: ${kdlData.pairingKey}")
                    }
                }

                logList.add(logString.toString())
                notifyLog()
            }
            evtPeripheralFirmwareUpdateComplete = {
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtPeripheralFirmwareUpdateComplete")
                logList.add(logString.toString())
                notifyLog()
            }

            evtPeripheralFirmwareUpdateFailure = {
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtPeripheralFirmwareUpdateFailure")
                logList.add(logString.toString())
                notifyLog()
            }

            evtPeripheralFirmwareUpdateProgress = { progressPercent, description ->
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtPeripheralFirmwareUpdateProgress\n")
                logString.append("Progress Percent: $progressPercent\n")
                logString.append("Description: $description")
                logList.add(logString.toString())
                notifyLog()
            }

            evtCannotConnect = { reason, description ->
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtCannotConnect\n")
                logString.append("Reason: $reason\n")
                logString.append("$reason: $description")
                logList.add(logString.toString())
                notifyLog()
            }

            evtC2PDataAcknowledged = { ID_HDR_MAJOR, ID_HDR_MINOR, description ->
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtC2PDataAcknowledged\n")
                logString.append("Major ID Header: $ID_HDR_MAJOR\n")
                logString.append("Minor ID Header: $ID_HDR_MINOR\n")
                logString.append("Description: $description")

                logList.add(logString.toString())
                notifyLog()
            }

            evtInternalParameters = { serviceUuid, baseCharacteristicUuid, companyIdentifier ->

                /*editor.apply {
                    putString(ConstantUtils.SERVICE_UUID,serviceUuid)
                    putString(ConstantUtils.BASE_CHARACTERISTIC_UUID,baseCharacteristicUuid)
                    putString(ConstantUtils.COMPANY_IDENTIFIER,companyIdentifier)
                }.commit()*/


                /*logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtInternalParameters\n")
                logString.append("Service UUID: $serviceUuid\n")
                logString.append("Base Characteristic UUID: $baseCharacteristicUuid\n")
                logString.append("Company Identifier: $companyIdentifier")
                logList.add(logString.toString())
                notifyLog()*/
            }

            evtP2CDiagnosticDataReceived = { DATA_PAYLOAD, description ->

                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtP2CDiagnosticDataReceived\n")
                logString.append("Payload: ${DATA_PAYLOAD.removeSpaces()}\n")
                logString.append("Description: $description")

                logList.add(logString.toString())
                notifyLog()
            }

            evtCurrentPeripheralPriorities =
                { priority1: String, priority2: String, priority3: String, description: String ->

                    logString.clear()
                    logString.append("\r------------\n")
                    logString.append(dateFormatter.format(Date()) + "\n")
                    logString.append("Event : SWLCentral.evtCurrentPeripheralPriorities\n")
                    logString.append("Priority 1: $priority1\n")
                    logString.append("Priority 2: $priority2\n")
                    logString.append("Priority 3: $priority3\n")
                    logString.append("Description: $description")
                    logList.add(logString.toString())
                    notifyLog()
                }

            evtPeripheralExceptionOccurred = { description ->
                logString.clear()
                logString.append("\r------------\n")
                logString.append(dateFormatter.format(Date()) + "\n")
                logString.append("Event : SWLCentral.evtPeripheralExceptionOccurred\n")

                logString.append("Description: $description")
                logList.add(logString.toString())
                notifyLog()
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.tvSwlCentralStatus.text = ConstantUtils.STATE_IDLE
        logAdapter = LogAdapter(logList)
        binding.rvLogs.adapter = logAdapter
        BleManager.getInstance().init(application)
        BleManager.INTERNAL_BUILD = false

        binding.btnClearLog.setOnClickListener {
            logString.clear()
            logList.clear()
            logAdapter.notifyDataSetChanged()

        }
        binding.btnSaveLog.setOnClickListener {
            if (logList.isEmpty()) return@setOnClickListener


            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TITLE, "log.txt")
            saveLogResultLauncher.launch(intent)
        }

        binding.toggleButton.isChecked = true


    }

    private var saveLogResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                try {
                    val output = contentResolver.openOutputStream(uri!!)
                    logList.forEach { output?.write(it.encodeToByteArray()) }
                    output?.flush()
                    output?.close()

                    showToast(this, "Log has been saved successfully.")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


        }


    override fun onResume() {
        super.onResume()
        BleManager.getInstance().registerListener(connectionEventListener)
        requestBluetoothPermission()
        if (!bluetoothAdapter.isEnabled) promptEnableBluetooth()
    }

    private fun requestBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT
                    ), 1001
                )
            }
        }
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ), 1001
                    )
                }
            } else {
                resultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) promptEnableBluetooth()

        }

    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().unregisterListener(connectionEventListener)
    }


    fun updateLogState(functionName: String) {
        logString.clear()
        logString.append("\r------------\n")
        logString.append(dateFormatter.format(Date()) + "\n")
        logString.append("State : $functionName")
        logList.add(logString.toString())
        notifyLog()

    }

    private fun saveLogToFile() {

        var file = File(this.getExternalFilesDir(null), "log.txt")
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(file)
            logList.forEach { os.write(it.encodeToByteArray()) }
            os.close()
            showToast(this, "Log has been saved successfully in ${file.absolutePath}.")
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun updateCentralState(state: String){
        binding.tvSwlCentralStatus.text = state
    }

    private fun notifyLog() {
        runOnUiThread {
            logAdapter.notifyDataSetChanged()
            if (binding.toggleButton.isChecked) {
                binding.rvLogs.smoothScrollToPosition(logList.size)
            }
        }
    }
}