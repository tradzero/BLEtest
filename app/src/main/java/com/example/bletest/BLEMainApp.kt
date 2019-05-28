package com.example.bletest

import android.app.Application
import android.util.Log
import com.snail.easyble.core.Ble
import com.snail.easyble.core.BleLogger

class BLEMainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        var init = Ble.instance.initialize(this)
        Log.d("test1", init.toString());
    }

    override fun onTerminate() {
        super.onTerminate()
        Ble.instance.disconnectAllConnections()
        Ble.instance.releaseAllConnections()
    }
}