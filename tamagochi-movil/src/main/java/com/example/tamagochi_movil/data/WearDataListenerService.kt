package com.example.tamagochi_movil.data

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.example.tamagochi_movil.presentation.MainViewModel

class WearDataListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/sensor_data") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val heartRate = dataMap.getFloat("heart_rate")
                    val acceleration = dataMap.getFloat("acceleration")
                    
                    Log.d("WearService", "Received: HR=$heartRate, Accel=$acceleration")
                    MainViewModel.instance.updateData(heartRate, acceleration)
                }
            }
        }
    }
}