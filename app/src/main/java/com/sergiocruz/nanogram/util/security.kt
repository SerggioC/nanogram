package com.sergiocruz.nanogram.util

import android.content.Context
import android.preference.PreferenceManager
import android.util.Base64
import com.sergiocruz.nanogram.R
import java.security.AccessControlContext

fun hasSavedToken(context: Context): Boolean {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPrefs.contains(context.getString(R.string.user_token))
}

fun getSavedToken(context: Context): String {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    return decode(sharedPrefs.getString(context.getString(R.string.user_token), "-1")!!)
}

fun encode(input: String) = String(Base64.encode(input.toByteArray(), Base64.DEFAULT))

fun decode(input: String) = String(Base64.decode(input.toByteArray(), Base64.DEFAULT))