package com.swaralink.external.activity
// File: <SplashActivity.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var binding: ActivitySplashBinding
    private val RC_LOCATION_REQUEST: Int = 1000
    private lateinit var locationRequest: LocationRequest
    private val isLocationPermissionGranted get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private val LOCATION_PERMISSION_REQUEST_CODE = 2
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        locationRequest = LocationRequest.create().apply {
            isWaitForAccurateLocation = true
            interval = 2000
            fastestInterval = 1000
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

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) requestLocationPermission()
        else {
            if (isLocationEnabled()) {
                gotoHome()
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
            gotoHome()
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
                    gotoHome()
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
            Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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
        }
    }
}