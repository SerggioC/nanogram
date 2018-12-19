package com.sergiocruz.nanogram.ui.main

import android.annotation.TargetApi
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.sergiocruz.nanogram.service.getRedirectUri


class AutenticationWebViewClient(private val tokenReceivedCallback: OnTokenReceived) : WebViewClient() {

    interface OnTokenReceived {
        fun onTokenReceived(token : String)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.url?.toString()?.startsWith(getRedirectUri(view?.context!!))!!) {
            val parts = request.url?.toString()?.split("=".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
            val requestToken = parts!![1]  // request token.
            tokenReceivedCallback.onTokenReceived(requestToken)
            return true
        }
        return false
    }

    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url?.startsWith(getRedirectUri(view?.context!!))!!) {
            val parts = url?.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val requestToken = parts[1]  // request token.
            tokenReceivedCallback.onTokenReceived(requestToken)
            return true
        }
        return false
    }


}

