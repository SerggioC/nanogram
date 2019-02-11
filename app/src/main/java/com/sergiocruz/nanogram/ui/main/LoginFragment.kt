package com.sergiocruz.nanogram.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.sergiocruz.nanogram.util.zoomInViewAnimation
import kotlinx.android.synthetic.main.login_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import android.animation.ValueAnimator
import android.animation.ArgbEvaluator


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
        loginButton.setOnClickListener {
            setLoginButtonStatus(LoginStatus.Loading)
            loadInstagramWebView()
        }
        testButton1.setOnClickListener { showButtonSuccess() }
        testButton2.setOnClickListener { showButtonFailAnim() }
        testButton3.setOnClickListener { showButtonLoading() }
    }

    sealed class LoginStatus {
        object Loading : LoginStatus()
        object Success : LoginStatus()
        object Failed : LoginStatus()
        object Canceled : LoginStatus()
    }

    private fun setLoginButtonStatus(status: LoginStatus) {
        when (status) {
            is LoginStatus.Loading -> showButtonLoading()
            is LoginStatus.Success -> showButtonSuccess()
            is LoginStatus.Failed -> showButtonFailAnim()
            is LoginStatus.Canceled -> showButtonReset()
        }
    }

    private fun showButtonReset() {
        loginButton.isEnabled = true
        loginButton.setText(R.string.login)
        loginButton.setBackgroundColor(ContextCompat.getColor(loginButton.context, R.color.colorAccent))
    }

    private fun showButtonFailAnim() {
        loginButton.isEnabled = true
        loginButton.setText(R.string.login)
        startColorAnimation(loginButton)
    }

    fun startColorAnimation(view: View) {
        val fromColor = ContextCompat.getColor(view.context, R.color.fail_red)
        val toColor = ContextCompat.getColor(view.context, R.color.colorAccent)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 450 // milliseconds
        colorAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()
    }

    private fun showButtonSuccess() {
        loginButton.isEnabled = false
        loginButton.setText(R.string.login_ok)
        loginButton.setBackgroundColor(ContextCompat.getColor(loginButton.context, R.color.ok_green))
        zoomInViewAnimation(loginButton)
    }

    private fun showButtonLoading() {
        loginButton.isEnabled = false
        loginButton.setText(R.string.loading)
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
        alertDialog.setOnDismissListener { showButtonReset() }
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
                        token?.let {
                            setLoginButtonStatus(LoginStatus.Success)
                            saveToken(context!!, token)
                        }
                        goToGridFragment()
                    } else {
                        Timber.e("Wrong response= $call ${response.errorBody()}")
                        setLoginButtonStatus(LoginStatus.Failed)
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Timber.e("fail on api call $call")
                    setLoginButtonStatus(LoginStatus.Failed)
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