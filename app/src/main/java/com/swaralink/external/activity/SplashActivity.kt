package com.swaralink.external.activity
// File: <SplashActivity.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.swaralinkble.hasPermission
import com.android.swaralinkble.requestPermission
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.swaralink.external.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val RC_LOCATION_REQUEST: Int = 1000
    private lateinit var locationRequest: LocationRequest
    private val isLocationPermissionGranted get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private val LOCATION_PERMISSION_REQUEST_CODE = 2
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationRequest = LocationRequest.create().apply {
            isWaitForAccurateLocation = true
            interval = 2000
            fastestInterval = 500
            priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY

        }
        checkLocationPermission()

    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted)
            requestLocationPermission()
        else {
            if (isLocationEnabled()) {
                //gotoHome()
                requestBluetoothPermission()
            } else {
                turnOnGps()
            }
        }
    }

    private fun gotoHome() {
        activityScope.launch {
            delay(100)
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun turnOnGps() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { response ->
            //gotoHome()
            requestBluetoothPermission()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, RC_LOCATION_REQUEST)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.i("TAG", "PendingIntent unable to execute request.")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_LOCATION_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    //gotoHome()
                    requestBluetoothPermission()
                } else if (resultCode == RESULT_CANCELED) {
                    requestLocationPermission()
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        requestPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    checkLocationPermission()
                }
            }
            1001 -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestBluetoothPermission()
                } else {
                    promptEnableBluetooth()
                }
            }
        }
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            resultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            gotoHome()
        }
    }

    private fun requestBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    1001
                )
            } else {
                promptEnableBluetooth()
            }
        } else {
            promptEnableBluetooth()
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                gotoHome()
                //promptEnableBluetooth()
            }

        }
}