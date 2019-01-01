package com.sergiocruz.nanogram.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sergiocruz.nanogram.BuildConfig
import com.sergiocruz.nanogram.Convert
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
import kotlinx.android.synthetic.main.image_item_layout.*
import kotlinx.android.synthetic.main.image_item_layout.view.*
import kotlinx.android.synthetic.main.main_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment(), ImagesAdapter.ImageClickListener,
    AutenticationWebViewClient.RedirectCallback {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var imagesAdapter: ImagesAdapter

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Convert.prepareTransitionsJava(this.context,  activity, images_recyclerview, this)
        })
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
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        }
        alertDialog.show()
    }

    override fun onRedirect(result: RedirectResult) {
        alertDialog.dismiss()
        showProgress(false)

        if (!result.code.isNullOrEmpty()) {
            getAccessToken(result.code)
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

    /** Prepares the shared element transition to the pager fragment,
     * as well as the other transitions that affect the flow. */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun prepareTransitions() {
        exitTransition =
                TransitionInflater.from(context)
                    .inflateTransition(R.transition.grid_exit_transition)

        // A similar mapping is set at the ArticlePagerFragment with a setEnterSharedElementCallback.
        //val exitSharedElementCallback =

        setExitSharedElementCallback(object : SharedElementCallback() {

            override fun onRejectSharedElements(rejectedSharedElements: MutableList<View>?) {
                super.onRejectSharedElements(rejectedSharedElements)
            }

            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
            }

            override fun onCaptureSharedElementSnapshot(
                sharedElement: View?,
                viewToGlobalMatrix: Matrix?,
                screenBounds: RectF?
            ): Parcelable {
                return super.onCaptureSharedElementSnapshot(
                    sharedElement,
                    viewToGlobalMatrix,
                    screenBounds
                )
            }

            override fun onSharedElementsArrived(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                listener: OnSharedElementsReadyListener?
            ) {
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener)
            }

            override fun onCreateSnapshotView(context: Context?, snapshot: Parcelable?): View {
                return super.onCreateSnapshotView(context, snapshot)
            }

            override fun onSharedElementStart(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                super.onSharedElementStart(
                    sharedElementNames,
                    sharedElements,
                    sharedElementSnapshots
                )
            }

            override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                super.onMapSharedElements(names, sharedElements)
                // Locate the ViewHolder for the clicked position.
                val selectedViewHolder =
                    images_recyclerview.findViewHolderForAdapterPosition(MainActivity.currentPosition)
                        ?: return

                // Map the first shared element name to the child ImageView.
                sharedElements[names[0]] = selectedViewHolder.itemView.image_item
            }
        })

        postponeEnterTransition()
    }


    override fun onImageClicked(adapterPosition: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gotoImageDetailsTransition(adapterPosition)
        } else {
            gotoImageDetails(adapterPosition)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gotoImageDetailsTransition(index: Int) {

        // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
        // instead of fading out with the rest to prevent an overlapping animation of fade and move).
        (exitTransition as android.transition.TransitionSet).excludeTarget(view, true)

        fragmentManager
            ?.beginTransaction()
            ?.setReorderingAllowed(true) // Optimize for shared element transition.
            ?.addSharedElement(image_item, image_item.transitionName)
            ?.add(R.id.container, DetailsFragment.newInstance(index))
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun gotoImageDetails(index: Int) {
        fragmentManager
            ?.beginTransaction()
            ?.add(R.id.container, DetailsFragment.newInstance(index))
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun getOut() {
        activity?.finish()
    }

}