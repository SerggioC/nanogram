package com.sergiocruz.nanogram

import android.app.Activity
import android.content.pm.PackageManager


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
