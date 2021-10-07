package com.swaralink.external.adapter
// File: <KnownDeviceAdapter.kt>
// Copyright (c) 2021, SwaraLink Technologies
// All Rights Reserved
// Licensed by SwaraLink Technologies, subject to terms of Software License Agreement
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.swaralinkble.KDData
import com.swaralink.external.R
import com.swaralink.external.databinding.AdapterKnownDevicesBinding

class KnownDeviceAdapter(
    var context : Context,
    var KDList: ArrayList<KDData>,
    val clickHandler: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var checkedPosition: Int =-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_known_devices,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).setData(KDList[position].deviceName)
    }

    override fun getItemCount(): Int {
        return KDList.size
    }

    inner class MyViewHolder(var dataBinding: AdapterKnownDevicesBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {

        fun setData(deviceName: String) {
            if(checkedPosition==-1)
                dataBinding.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
            else
            {
                if(checkedPosition ==adapterPosition)
                    dataBinding.root.setBackgroundColor(ContextCompat.getColor(context,R.color.gray))
                else dataBinding.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
            }
            dataBinding.tvDeviceName.text = deviceName
            dataBinding.tvDeviceName.setOnClickListener {
                clickHandler.invoke(adapterPosition)
                dataBinding.root.setBackgroundColor(ContextCompat.getColor(context,R.color.gray))
                if(checkedPosition!=adapterPosition)
                {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
            }
        }
    }
}