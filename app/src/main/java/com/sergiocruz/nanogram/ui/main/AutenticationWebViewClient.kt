package com.sergiocruz.nanogram.ui.main

import android.annotation.TargetApi
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sergiocruz.nanogram.service.getRedirectUri


class AutenticationWebView : WebViewClient() {

    private lateinit var requestToken: String

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.url?.toString()?.startsWith(getRedirectUri(view?.context!!))!!) {
            val parts = request?.url?.toString()?.split("=".toRegex())?.dropLastWhile { it.isEmpty() }.toTypedArray()
            requestToken = parts[1]  // request token.
            this@InstagramLoginDialog.dismiss()
            return true
        }
        return false
    }

    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url?.startsWith(getRedirectUri(view?.context!!))!!) {
            val parts = url?.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            requestToken = parts[1]  // request token.
            this@InstagramLoginDialog.dismiss()
            return true
        }
        return false

    }


}

