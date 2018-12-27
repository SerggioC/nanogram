package com.sergiocruz.nanogram.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sergiocruz.nanogram.BuildConfig
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.adapter.ImagesAdapter
import com.sergiocruz.nanogram.adapter.MyItemDecoration
import com.sergiocruz.nanogram.model.RedirectResult
import com.sergiocruz.nanogram.model.Token
import com.sergiocruz.nanogram.retrofit.InstagramApiControler
import com.sergiocruz.nanogram.service.getInstagramUrl
import com.sergiocruz.nanogram.service.getRedirectUri
import com.sergiocruz.nanogram.util.InfoLevel.ERROR
import com.sergiocruz.nanogram.util.encode
import com.sergiocruz.nanogram.util.hasSavedToken
import com.sergiocruz.nanogram.util.showToast
import kotlinx.android.synthetic.main.main_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment(), ImagesAdapter.ImageClickListener,
    AutenticationWebViewClient.RedirectCallback {

    companion object {
        fun newInstance() = MainFragment()
        private const val PERMISSION_REQUESTS_CODE = 1
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var imagesAdapter: ImagesAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (!allPermissionsGranted()) getRuntimePermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProgress(true)
        if (hasSavedToken(this.context!!)) {
            initializeRecyclerView()
        } else {
            loadInstagramWebView()
        }
    }

    private fun initializeRecyclerView() {
        imagesAdapter = ImagesAdapter(this)
        images_recyclerview?.layoutManager =
                StaggeredGridLayoutManager(resources.getInteger(R.integer.span_count), VERTICAL)
        images_recyclerview?.setHasFixedSize(false)
        images_recyclerview?.addItemDecoration(MyItemDecoration(10))
        images_recyclerview?.adapter = imagesAdapter
        initializeViewModel()
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.getUserMedia(this.context!!).observe(this, Observer {
            imagesAdapter.swap(it)
            showProgress(false)
        })
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in getRequiredPermissions()) {
            if (!isPermissionGranted(permission!!)) {
                return false
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val allNeededPermissions: ArrayList<String> = ArrayList(0)
        for (permission in getRequiredPermissions()) {
            if (!isPermissionGranted(permission!!)) {
                allNeededPermissions.add(permission)
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                activity as Activity, allNeededPermissions.toTypedArray(),
                PERMISSION_REQUESTS_CODE
            )
        }
    }

    /** Read and return manifest uses-permission fields */
    private fun getRequiredPermissions(): Array<String?> {
        return try {
            val info = this.context?.packageManager?.getPackageInfo(
                this.activity?.packageName,
                PackageManager.GET_PERMISSIONS
            )
            val permissions = info?.requestedPermissions
            if (permissions != null && permissions.isNotEmpty()) {
                permissions
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            arrayOfNulls(0)
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        val granted = ContextCompat.checkSelfPermission(
            this.context!!,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        Log.i(
            "Sergio> Sergio>",
            "Permission $permission ${if (granted) "granted" else "NOT granted"}"
        )
        return granted
    }

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
            showToast(
                this.context, "Error Authorizing! \n" +
                        "${result.error} \n" +
                        "${result.errorDescription} \n" +
                        "${result.errorReason} \n" +
                        "Try again",
                ERROR
            )
            getOut()
        }
    }

    private fun getAccessToken(accessCode: String) {
        Log.i("Sergio> ", "accessCode is: $accessCode")

        val apiController = InstagramApiControler().apiController
        apiController?.getAcessCode(
            BuildConfig.ClientId,
            BuildConfig.ClientSecret,
            "authorization_code",
            getRedirectUri(this.context!!),
            accessCode
        )?.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    Log.i("Sergio> ", "response token= $token")
                    Log.i("Sergio> ", "getting user media")
                    token?.let { saveToken(token) }
                    initializeRecyclerView()
                } else {
                    Log.i(
                        "Sergio> ",
                        "Wrong response= $call ${response.errorBody()}"
                    )
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Log.i("Sergio> ", "fail on api call " + call.toString())
            }

        })
    }


    internal fun saveToken(token: String) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context).edit()
        val encoded = encode(token)
        sharedPrefs.putString(getString(R.string.user_token), encoded).apply()
    }

    /**
     * Shows the progress UI
     */
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        val shortAnimTime =
            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
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

    override fun onImageClicked(adapterPosition: Int) {
        gotoImageDetails(adapterPosition)
    }

    private fun gotoImageDetails(index: Int) {
        fragmentManager?.beginTransaction()
            ?.add(R.id.container, DetailsFragment.newInstance(index))
            ?.addToBackStack(null)
            ?.commit()

    }

    private fun getOut() {
        activity?.finish()
    }

}