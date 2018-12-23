package com.sergiocruz.nanogram.util

import android.content.Context
import android.preference.PreferenceManager
import android.util.Base64
import com.sergiocruz.nanogram.R

fun hasSavedToken(context: Context): Boolean {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPrefs.contains(context.getString(R.string.user_token))
}

fun getSavedToken(context: Context): String {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    return decode(sharedPrefs.getString(context.getString(R.string.user_token), "-1")!!)
}

/** TODO: create tougher security */
fun encode(input: String) = String(Base64.encode(input.toByteArray(), Base64.DEFAULT))

/** TODO: create tougher security */
fun decode(input: String) = String(Base64.decode(input.toByteArray(), Base64.DEFAULT))