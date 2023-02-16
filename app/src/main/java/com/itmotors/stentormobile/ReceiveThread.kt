package com.itmotors.stentormobile

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.*

@OptIn(ExperimentalUnsignedTypes::class)
class ReceiveThread(bSocket: BluetoothSocket, private val listener: Listener) : Thread() {
    var inStream: InputStream? = null
    var outStream: OutputStream? = null

    init {
        try {
            inStream = bSocket.inputStream
        } catch (i: IOException){

        }
        try {
            outStream = bSocket.outputStream
        } catch (i: IOException){

        }
    }

    override fun run() {
        listener.onStringReceive("rThread started", "85FB5A")
        val buf = ByteArray(1024)
        var totalSize : Int
        var bytesRead : Int

        while (true){
            try{
                totalSize = 0
                val tmp = ByteArray(256)
                while (!CRC16.verifyCRC(tmp.toUByteArray()) || empty(tmp))
                {
                    bytesRead = inStream?.read(buf)!!
                    //Log.d("ReceiveThread", "Bytes received: $bytesRead")
                    for (i in 0 until bytesRead){
                        tmp[totalSize] = buf[i]
                        totalSize++
                    }
                    if (totalSize > 55) break
                }
                listener.onByteReceive(tmp.toUByteArray(), totalSize)

            } catch (i: IOException){
                listener.onStringReceive("Соединение прервано", "#EA0903")
                Log.d("ReceiveThread", "Connection lost")
                break
            }
        }
    }


    fun sendMessage(byteArray: ByteArray){
        try {
            outStream?.write(byteArray)
        } catch (i: IOException){
            Log.d("ReceiveThread", "Cannot send data")
        }
    }

    private fun empty(data: ByteArray): Boolean
    {
        val emptyArray = ByteArray(data.size)
        return data.contentEquals(emptyArray)
    }

    interface Listener{
        fun onStringReceive(txt: String, color: String)
        fun onByteReceive(data: UByteArray, size: Int)
    }
}