package com.itmotors.stentormobile

import android.util.Log
import kotlin.concurrent.thread

class WorkMode(private val btConnection: BtConnection) {

    private val waitTimeout = 50L
    private val sendTimeout = 100L
    private val pauseTimeout = 250L
    private val readTimeout = 400L

    private var isSending: Boolean = true
    private var isPaused: Boolean = false

    private var lockingObject = object {}

    private var responseCount: Int = 0
    private var attempt: Int = 0

    private var command: UByte = 0u

    private lateinit var readThread: Thread
    private lateinit var sendThread: Thread
    private lateinit var pauseThread: Thread


    fun start(){
        isPaused = false
        isSending = true
        readThread = thread(isDaemon = true){
            while (isSending){
                if (!tryPause()){
                    Log.d("WorkMode", "ReadThread: trypause false")
                    Thread.sleep(waitTimeout)

                    continue
                }
                Log.d("WorkMode", "ReadThread: read request sending")
                btConnection.sendData(STENTORController.makeReadRequest())
                Thread.sleep(readTimeout)
            }
        }
        sendThread = thread(isDaemon = true){
            while (isSending){
                if (command == 0.toUByte()){
                    //Log.d("WorkMode", "SendThread: command = 0, sendthread sleep")
                    Thread.sleep(sendTimeout)
                    continue
                }
                if (tryPause()){
                    Log.d("WorkMode", "SendThread: All threads paused")
                    Log.d("WorkMode", "SendThread: Sending command $command, attempt $attempt")
                    btConnection.sendData(STENTORController.makeWriteRequest(command))
                }
                Thread.sleep(sendTimeout)
            }
        }

        pauseThread = thread(isDaemon = true){
            var lastResponseIndex = responseCount
            while (isSending){
                //Log.d("WorkMode", "PauseThread: sleep")
                Thread.sleep(pauseTimeout)
                synchronized(lockingObject){
                    if (isPaused)
                    {
                        Log.d("WorkMode", "PauseThread: paused")
                        if (responseCount == lastResponseIndex) resumeSending()
                        else lastResponseIndex = responseCount
                    }
                }
            }
        }
    }

    private fun tryPause() : Boolean{
        synchronized(lockingObject){
            if (isPaused){
                Log.d("WorkMode", "Pause failed")
                return false

            }
            if (responseCount == Int.MAX_VALUE) responseCount = 0
            else responseCount++
            isPaused = true
            Log.d("WorkMode", "Pause successful")
            return true
        }
    }

    fun resumeSending() {
        if (attempt == 3){
            Log.d("WorkMode", "too much attempts, resetting")
            attempt = 0
            command = 0u
        }
        isPaused = false
        Log.d("WorkMode", "Sending unpaused")

    }

    fun putCommand(cmd: UByte){
            command = cmd
            attempt = 0
            Log.d("WorkMode", "Command set to $cmd")
    }

    fun stop(){
        isSending = false
    }

}