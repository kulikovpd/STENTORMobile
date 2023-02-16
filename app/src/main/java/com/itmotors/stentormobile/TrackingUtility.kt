package com.itmotors.stentormobile


import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtility {
    fun hasPermissions(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }else {
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
}

