package com.sergiocruz.nanogram

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val PERMISSION_REQUESTS_CODE = 1

/** Read and return manifest uses-permission fields */
fun getRequiredPermissions(activity: Activity): Array<String?> {
    return try {
        val info = activity.packageManager
            .getPackageInfo(activity.packageName, PackageManager.GET_PERMISSIONS)
        val permissions = info.requestedPermissions
        if (permissions != null && permissions.isNotEmpty()) {
            permissions
        } else {
            arrayOfNulls(0)
        }
    } catch (e: Exception) {
        arrayOfNulls(0)
    }
}

fun allPermissionsGranted(activity: Activity): Boolean {
    for (permission in getRequiredPermissions(activity)) {
        if (!isPermissionGranted(permission!!, activity)) {
            return false
        }
    }
    return true
}

fun getRuntimePermissions(activity: Activity) {
    val allNeededPermissions: ArrayList<String> = ArrayList(0)
    for (permission in getRequiredPermissions(activity)) {
        if (!isPermissionGranted(permission!!, activity)) {
            allNeededPermissions.add(permission)
        }
    }

    if (!allNeededPermissions.isEmpty()) {
        ActivityCompat.requestPermissions(
            activity, allNeededPermissions.toTypedArray(),
            PERMISSION_REQUESTS_CODE
        )
    }
}

fun isPermissionGranted(permission: String, activity: Activity): Boolean {
    val granted = ContextCompat.checkSelfPermission(activity, permission) ==
            PackageManager.PERMISSION_GRANTED
    Log.i(
        "Sergio> Sergio>",
        "Permission $permission ${if (granted) "granted" else "NOT granted"}"
    )
    return granted
}
