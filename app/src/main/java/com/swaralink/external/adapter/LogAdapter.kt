package com.swaralink.external.adapter
// File: <LogAdapter.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.swaralink.external.R
import com.swaralink.external.databinding.AdapterLogBinding

class LogAdapter(var logList : ArrayList<String>)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_log,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).setData(logList[position])
    }

    override fun getItemCount(): Int {
        return  logList.size
    }

    inner class MyViewHolder(var dataBinding: AdapterLogBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {

        fun setData(log: String) {
            dataBinding.tvLog.text = log
        }
    }
}