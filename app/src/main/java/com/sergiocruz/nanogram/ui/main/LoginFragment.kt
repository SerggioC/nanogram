package com.sergiocruz.nanogram.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.sergiocruz.nanogram.BuildConfig
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.RedirectResult
import com.sergiocruz.nanogram.model.Token
import com.sergiocruz.nanogram.retrofit.AppApiController
import com.sergiocruz.nanogram.service.getInstagramUrl
import com.sergiocruz.nanogram.service.getRedirectUri
import com.sergiocruz.nanogram.util.InfoLevel.ERROR
import com.sergiocruz.nanogram.util.saveToken
import com.sergiocruz.nanogram.util.showToast
import kotlinx.android.synthetic.main.login_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LoginFragment : Fragment(),
    AutenticationWebViewClient.RedirectCallback {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginButton.setOnClickListener { loadInstagramWebView() }
    }

    private fun goToGridFragment() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.container, GridFragment())
            ?.commit()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getAuthWebView(): WebView {
        val webView = WebView(this.context)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = AutenticationWebViewClient(this)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(getInstagramUrl(this.context!!))
        return webView
    }

    private lateinit var alertDialog: AlertDialog

    private fun loadInstagramWebView() {
        alertDialog = AlertDialog.Builder(this.context!!)
            .setView(getAuthWebView())
            .create()
        // show software input keyboard for alert dialog
        alertDialog.setOnShowListener {
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        }
//        alertDialog.setOnDismissListener {
//            // when onbackpressed and not authorized
//            if (!authorized) getOut()
//        }
        alertDialog.show()
    }

    override fun onRedirect(result: RedirectResult) {
        authorized = true
        alertDialog.dismiss()

        if (!result.code.isNullOrEmpty()) {
            getAccessToken(result.code)
        } else {
            showToast(
                context, "Error Authorizing! \n" +
                        "${result.error} \n" +
                        "${result.errorDescription} \n" +
                        "${result.errorReason} \n" +
                        "Try again",
                ERROR
            )
            //getOut()
        }
    }

    private var authorized: Boolean = false

    private fun getAccessToken(accessCode: String) {
        Timber.i("accessCode is: $accessCode")

        AppApiController.apiController
            ?.getAccessCode(
                BuildConfig.ClientId,
                BuildConfig.ClientSecret,
                "authorization_code",
                getRedirectUri(this.context!!),
                accessCode
            )?.enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        Timber.i("response token= $token")
                        Timber.i("getting user media")
                        token?.let { saveToken(context!!, token) }
                        goToGridFragment()
                    } else {
                        Timber.i(
                            "Wrong response= $call ${response.errorBody()}"
                        )
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Timber.i("fail on api call $call")
                }

            })
    }
//
//    /** Shows the progress UI */
//    private fun showProgress(show: Boolean) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//
//        val shortAnimTime =
//            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
//        loginProgress.visibility = if (show) View.VISIBLE else View.GONE
//        loginProgress.animate()
//            .setDuration(shortAnimTime)
//            .alpha((if (show) 1 else 0).toFloat())
//            .setListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator) {
//                    loginProgress.visibility = if (show) View.VISIBLE else View.GONE
//                }
//            })
//
//    }
//
//    private fun getOut() {
//        activity?.finish()
//    }

}