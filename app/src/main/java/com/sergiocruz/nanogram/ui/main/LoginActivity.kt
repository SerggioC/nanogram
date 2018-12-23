package com.sergiocruz.nanogram.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sergiocruz.nanogram.BuildConfig
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.RedirectResult
import com.sergiocruz.nanogram.model.Token
import com.sergiocruz.nanogram.retrofit.InstagramApiControler
import com.sergiocruz.nanogram.service.getInstagramUrl
import com.sergiocruz.nanogram.service.getRedirectUri
import com.sergiocruz.nanogram.util.InfoLevel.ERROR
import com.sergiocruz.nanogram.util.InfoLevel.INFO
import com.sergiocruz.nanogram.util.encode
import com.sergiocruz.nanogram.util.hasSavedToken
import com.sergiocruz.nanogram.util.showToast
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(), AutenticationWebViewClient.RedirectCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (!allPermissionsGranted()) {
            getRuntimePermissions()
        }

        if (hasSavedToken(this)) {
            showProgress(false)
            goToMainActivity()
        } else {
            loadInstagramWebView()
        }

        // Authorize app with instagram
        authorize_button.setOnClickListener {
            showProgress(true)

            if (hasSavedToken(this)) {
                showProgress(false)
                goToMainActivity()
            } else {
                loadInstagramWebView()
            }

        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission!!)) {
                return false
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val allNeededPermissions: ArrayList<String> = ArrayList(0)
        for (permission in getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission!!)) {
                allNeededPermissions.add(permission)
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                this, allNeededPermissions.toTypedArray(),
                PERMISSION_REQUESTS_CODE
            )
        }
    }

    /** Read and return manifest uses-permission fields */
    private fun getRequiredPermissions(): Array<String?> {
        return try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
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

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        val granted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        Log.i(
            "Sergio> Sergio>",
            "Permission $permission ${if (granted) "granted" else "NOT granted"}"
        )
        return granted
    }

    private fun getAuthWebView(): WebView {
        val webView = WebView(this)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = AutenticationWebViewClient(this)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(getInstagramUrl(this))
        return webView
    }

    private lateinit var alertDialog: AlertDialog

    private fun loadInstagramWebView() {
        alertDialog = AlertDialog.Builder(this)
            .setView(getAuthWebView())
            .create()
        alertDialog.setOnShowListener {
            alertDialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        }
        alertDialog.show()
    }

    override fun onRedirect(result: RedirectResult) {
        alertDialog.dismiss()
        showProgress(false)

        if (!result?.code.isNullOrEmpty()) {
            getAccessToken(result.code!!)
        } else {
            showToast(this, "Error Authorizing! \n" +
                    "${result.error} \n" +
                    "${result.errorDescription} \n" +
                    "${result.errorReason} \n" +
                    "Try again",
                ERROR
            )
        }
    }

    private fun getAccessToken(accessCode: String) {
        showToast(this, "Access Code is $accessCode", INFO)

        val apiController = InstagramApiControler().apiController
        apiController?.getAcessCode(
            BuildConfig.ClientId,
            BuildConfig.ClientSecret,
            "authorization_code",
            getRedirectUri(this),
            accessCode
        )?.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    Log.i("Sergio> ", "response token= " + token)
                    Log.i("Sergio> ", "getting user media")
                    token?.let { saveToken(token) }
                    goToMainActivity()
                } else {
                    Log.i(
                        "Sergio> ",
                        "Wrong response= " + call.toString() + " " + response.errorBody()
                    )
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Log.i("Sergio> ", "fail on api call " + call.toString())
            }

        })
    }

    internal fun saveToken(token: String) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this).edit()
        val encoded = encode(token)
        sharedPrefs.putString(getString(R.string.user_token), encoded).apply()
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        val shortAnimTime =
            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })

    }


    private fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    companion object {

        /**
         * Id to identity  permission request.*/
        private const val PERMISSION_REQUESTS_CODE = 1

    }
}
