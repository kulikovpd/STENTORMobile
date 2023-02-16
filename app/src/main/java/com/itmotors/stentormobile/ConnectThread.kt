package com.itmotors.stentormobile


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class ConnectThread(device: BluetoothDevice, private val listener: ReceiveThread.Listener) : Thread() {
    private val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    private var mSocket: BluetoothSocket? = null


    lateinit var rThread: ReceiveThread
    init {
        try {
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }catch (i: IOException){

        }
    }

    override fun run() {
        try {

            listener.onStringReceive("Соединение...", "#ECE120")
            mSocket?.connect()
            //Log.d("ConnectThread","Connected")
            rThread = ReceiveThread(mSocket!!, listener)
            rThread.start()
            listener.onStringReceive("Соединение установлено", "#85FB5A")

        }catch (i: IOException){
            if (this.isActive()) listener.onStringReceive("Соединение прервано", "#EA0903")
                else listener.onStringReceive("Не удалось соединиться", "#EA0903")
            Log.d("MyLog","Cant connect to device")
        }
    }

    fun isActive() :Boolean{
        return this::rThread.isInitialized
    }

    fun closeConnection(){
        try{
            rThread.inStream?.close()
        }
        catch (i : IOException){

        }
        try{
            rThread.outStream?.close()
        }
        catch (i : IOException){

        }
        try {
            mSocket?.close()
            listener.onStringReceive("Соединение не установлено", "#EA0903")
        }catch (i: IOException){

        }
    }
}