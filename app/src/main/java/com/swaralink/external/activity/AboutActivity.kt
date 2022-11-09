package com.swaralink.external.activity
// File: <AboutActivity.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.swaralinkble.ConstantUtils
import com.android.swaralinkble.getAppVersionName
import com.swaralink.external.R
import com.swaralink.external.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.layoutToolbar.tvToolbarTitle.text = getString(R.string.about)
        binding.layoutToolbar.imgToolbarLeft.setOnClickListener { onBackPressed() }

        binding.tvVersion.text = getString(R.string.app_version, "v${getAppVersionName(this)}")
        binding.tvDate.text = getString(R.string.date, "09-Nov-2022")
        /*val dateObj: Date = Calendar.getInstance().time
        val dateStr =  SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH).format(dateObj)
        tvDate.text = dateStr*/

        //binding.tvServiceUUID.text=ConstantUtils.DEFAULT_SERVICE_UUID



    }
}