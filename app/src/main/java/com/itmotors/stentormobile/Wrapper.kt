package com.itmotors.stentormobile

import android.app.Activity
import android.os.Build
import java.io.Serializable
import java.nio.ByteBuffer


@OptIn(ExperimentalUnsignedTypes::class)
class Wrapper {

    companion object{
        fun wrapShort(byte1: UByte, byte2: UByte): Short{
            return ByteBuffer.wrap(
                ubyteArrayOf(
                    byte1,
                    byte2
                ).toByteArray()).short
        }

        fun wrapInt(byte1: UByte, byte2: UByte, byte3: UByte, byte4: UByte): Int{
            return ByteBuffer.wrap(
                ubyteArrayOf(
                    byte1,
                    byte2,
                    byte3,
                    byte4
                ).toByteArray()).int
        }

        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
        {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                activity.intent.getSerializableExtra(name, clazz)!!
            else
                activity.intent.getSerializableExtra(name) as T
        }
    }
}