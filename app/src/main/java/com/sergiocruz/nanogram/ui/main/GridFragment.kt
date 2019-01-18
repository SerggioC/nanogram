package com.sergiocruz.nanogram.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.sergiocruz.nanogram.BuildConfig
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.adapter.GridImageAdapter
import com.sergiocruz.nanogram.model.RedirectResult
import com.sergiocruz.nanogram.model.Token
import com.sergiocruz.nanogram.retrofit.AppApiController
import com.sergiocruz.nanogram.service.getInstagramUrl
import com.sergiocruz.nanogram.service.getRedirectUri
import com.sergiocruz.nanogram.util.*
import com.sergiocruz.nanogram.util.InfoLevel.ERROR
import kotlinx.android.synthetic.main.grid_fragment.*
import kotlinx.android.synthetic.main.grid_fragment.view.*
import kotlinx.android.synthetic.main.item_image_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class GridFragment : Fragment(),
    GridImageAdapter.ImageClickListener,
    AutenticationWebViewClient.RedirectCallback {

    private var recyclerView: RecyclerView? = null

    private lateinit var viewModel: MainViewModel
    private lateinit var gridImageAdapter: GridImageAdapter
    //private lateinit var layoutManager: StaggeredGridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.grid_fragment, container, false)

        recyclerView = rootView.images_recyclerview

        showProgress(true, rootView.login_progress)
        if (hasSavedToken(this.context!!)) {
            initializeRecyclerView(recyclerView)
        } else {
            loadInstagramWebView()
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollToPosition()
    }

    override fun onResume() {
        super.onResume()
        exitFullScreen(activity)
    }

    private fun initializeRecyclerView(images_recyclerview: RecyclerView?) {
        // Defined in XML
        val layoutManager =
            StaggeredGridLayoutManager(resources.getInteger(R.integer.span_count), VERTICAL)
        images_recyclerview?.layoutManager = layoutManager
        images_recyclerview?.setHasFixedSize(false)
        //images_recyclerview?.addItemDecoration(MyItemDecoration(1))

        gridImageAdapter = GridImageAdapter(this, this, getImageWidth(this.activity as Activity))
        gridImageAdapter.setHasStableIds(true)
        images_recyclerview?.adapter = gridImageAdapter

        initializeViewModel()
//        initializeMock()
    }

    private fun initializeMock() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            prepareExitTransitions()
            postponeEnterTransition()
        }
        //showProgress(false, login_progress)


    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.getUserMedia(this.context!!).observe(this, Observer {
            gridImageAdapter.swap(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                prepareExitTransitions()
                postponeEnterTransition()
            }
            showProgress(false, login_progress)
            scrollToPosition()
        })
    }

    private fun scrollToPosition() {
        images_recyclerview.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                view: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                images_recyclerview.removeOnLayoutChangeListener(this)

                val layoutManager = recyclerView?.layoutManager
                val viewAtPosition =
                    layoutManager?.findViewByPosition(MainActivity.currentPosition)

                // Scroll to position if the view for the current position is null
                // (not currently part of layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(
                        viewAtPosition,
                        false,
                        true
                    )
                ) {
                    images_recyclerview.post {
                        layoutManager?.scrollToPosition(MainActivity.currentPosition)
                    }
                }

            }
        })
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
        alertDialog.setOnDismissListener {
            // when onbackpressed and not authorized
            if (!authorized) getOut()
        }
        alertDialog.show()
    }

    override fun onRedirect(result: RedirectResult) {
        authorized = true
        alertDialog.dismiss()
        showProgress(false, login_progress)

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

    private var authorized: Boolean = false

    private fun getAccessToken(accessCode: String) {
        Timber.i("accessCode is: $accessCode")

        val apiController = AppApiController().apiController
        apiController?.getAccessCode(
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
                    initializeRecyclerView(images_recyclerview)
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

    /** Shows the progress UI */
    private fun showProgress(show: Boolean, login_progress: ProgressBar) {
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
    private fun prepareExitTransitions() {
        exitTransition = TransitionInflater.from(activity)
            .inflateTransition(R.transition.grid_exit_transition)
            .setDuration(resources.getInteger(R.integer.transition_duration).toLong())

        // A similar mapping is set at the ImageFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    Timber.d("entering onmapsharedelements MainActivity.currentPosition = ${MainActivity.currentPosition}")

                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder =
                        recyclerView?.findViewHolderForAdapterPosition(MainActivity.currentPosition)
                    if (selectedViewHolder == null) return


//                val imageVar = viewModel.getImageVarForIndex(MainActivity.currentPosition, context!!)
//                val transitionName = imageVar.images?.standardResolution.hashCode().toString()

                    // Map the first shared element name to the child ImageView.
                    val name = selectedViewHolder.itemView.item_image
                    sharedElements[names[0]] = name
                    Timber.i("onexitonMapname transition Name= ${name.transitionName}}")
                }
            })

    }

    override fun onImageClicked(adapterPosition: Int, view: View) {
        MainActivity.currentPosition = adapterPosition

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gotoImageDetailsTransition(adapterPosition, view)
        } else {
            gotoImageDetails(adapterPosition)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gotoImageDetailsTransition(index: Int, view: View) {

        (exitTransition as TransitionSet).excludeTarget(view, true)

        Timber.i("onImage clicked: position: $index")

        fragmentManager
            ?.beginTransaction()
            ?.setReorderingAllowed(true) // Optimize for shared element transition.
            ?.addSharedElement(view, view.transitionName)
            ?.replace(
                R.id.container,
                DetailsViewPagerFragment(),
                DetailsViewPagerFragment::class.java.simpleName
            )
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun gotoImageDetails(index: Int) {
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.container, DetailsViewPagerFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun getOut() {
        activity?.finish()
    }

}