package com.sergiocruz.nanogram.service

import android.content.Context
import com.sergiocruz.nanogram.BuildConfig
import com.sergiocruz.nanogram.R

/** 'Random' valid redirect URL */
fun getRedirectUri(context: Context): String {
    return context.getString(R.string.scheme) + context.getString(R.string.host) + context.getString(
        R.string.pathPrefix
    )
}

/** Authorize dev(client id) to use instagram account */
fun getInstagramUrl(context: Context): String {
    return context.getString(R.string.insta_api_uri_base) +
            "client_id=${BuildConfig.ClientId}&" +
            "redirect_uri=${getRedirectUri(context)}&" +
            "response_type=code"
}
