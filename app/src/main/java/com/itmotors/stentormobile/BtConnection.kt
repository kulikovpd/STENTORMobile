package com.itmotors.stentormobile

import android.bluetooth.BluetoothAdapter


class BtConnection(private val adapter: BluetoothAdapter, private val listener: ReceiveThread.Listener) {
    private lateinit var cThread: ConnectThread

    fun connect(mac: String) {
        if (adapter.isEnabled && mac.isNotEmpty()) {
            val device = adapter.getRemoteDevice(mac)
            device.let {
                cThread = ConnectThread(it, listener)
                cThread.start()
            }
        }
    }

    fun isActive() : Boolean{
        if (this::cThread.isInitialized){
            if (cThread.isActive()){
                return true
            }
        }
        return false
    }

    fun sendData(data: ByteArray){
        cThread.rThread.sendMessage(data)
    }

    fun closeConnection(){
        cThread.closeConnection()
    }
}