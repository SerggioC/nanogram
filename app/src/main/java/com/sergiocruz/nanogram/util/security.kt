package com.sergiocruz.nanogram.util

import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import com.sergiocruz.nanogram.R


fun hasSavedToken(context: Context): Boolean {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPrefs.contains(context.getString(R.string.user_token))
}

fun getSavedToken(context: Context): String {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    return decode(sharedPrefs.getString(context.getString(R.string.user_token), "-1")!!)
}

fun saveToken(context: Context, token: String) {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
    val encoded = encode(token)
    sharedPrefs.putString(context.getString(R.string.user_token), encoded).apply()
}

fun deleteToken(context: Context) {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
    sharedPrefs.remove(context.getString(R.string.user_token)).apply()
    clearCookies(context)
}

/** TODO: create tougher security */
fun encode(input: String) = String(Base64.encode(input.toByteArray(), Base64.DEFAULT))

/** TODO: create tougher security */
fun decode(input: String) = String(Base64.decode(input.toByteArray(), Base64.DEFAULT))


fun clearCookies(context: Context) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
        val cookieSyncMngr = CookieSyncManager.createInstance(context)
        cookieSyncMngr.startSync()
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        cookieManager.removeSessionCookie()
        cookieSyncMngr.stopSync()
        cookieSyncMngr.sync()
    } else {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

//    webView.clearCache(true)
//    webView.clearHistory()

}