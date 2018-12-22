package com.sergiocruz.nanogram.ui.main

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sergiocruz.nanogram.model.RedirectResult
import com.sergiocruz.nanogram.service.getRedirectUri


class AutenticationWebViewClient(private val onRedirectCallback: RedirectCallback) :
    WebViewClient() {

    interface RedirectCallback {
        fun onRedirect(result: RedirectResult)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.url?.toString()?.startsWith(getRedirectUri(view?.context!!))!!) {
            onRedirectCallback.onRedirect(getRedirectResult(request.url))
            return true
        }
        return false
    }

    private fun getRedirectResult(url: Uri): RedirectResult {
        val error = url.getQueryParameter("error")
        val errorReason = url.getQueryParameter("error_reason")
        val errorDescription = url.getQueryParameter("error_description")
        val code = url.getQueryParameter("code")
        return RedirectResult(code, error, errorReason, errorDescription)
    }



}

