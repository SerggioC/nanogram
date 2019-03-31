package com.sergiocruz.nanogram.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.adapter.GridImageAdapter
import com.sergiocruz.nanogram.ui.BluetoothShare
import com.sergiocruz.nanogram.util.exitFullScreen
import com.sergiocruz.nanogram.util.getImageWidth
import com.sergiocruz.nanogram.util.hasSavedToken
import kotlinx.android.synthetic.main.grid_fragment.*
import kotlinx.android.synthetic.main.item_image_layout.view.*
import timber.log.Timber
import java.io.File

class GridFragment : Fragment(),
    GridImageAdapter.ImageClickListener, GridImageAdapter.ImageLongClickListener {
    private lateinit var viewModel: MainViewModel
    private lateinit var gridImageAdapter: GridImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.grid_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProgress(true)
        if (hasSavedToken(context)) {
            initializeRecyclerView()
        } else {
            activity?.onBackPressed()
        }

        scrollToPosition()
    }

    override fun onResume() {
        super.onResume()
        exitFullScreen(activity)
    }

    private fun initializeRecyclerView() {
        // Defined in XML
        val layoutManager =
            StaggeredGridLayoutManager(resources.getInteger(R.integer.span_count), VERTICAL)
        images_recyclerview?.layoutManager = layoutManager
        images_recyclerview?.setHasFixedSize(false)
        //images_recyclerview?.addItemDecoration(MyItemDecoration(1))

        gridImageAdapter = GridImageAdapter(this, this, this, getImageWidth(activity as Activity))
        gridImageAdapter.setHasStableIds(true)
        images_recyclerview?.adapter = gridImageAdapter

        initializeViewModel()

    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.getUserMedia().observe(this, Observer {
            if (it == null) {
                noNetworkImageView.visibility = View.VISIBLE
            } else {
                gridImageAdapter.swap(it)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    prepareExitTransitions()
                    postponeEnterTransition()
                }
                showProgress(false)
                scrollToPosition()
            }

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

                val layoutManager = images_recyclerview?.layoutManager
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

    /** Shows the progress UI */
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        val shortAnimTime =
            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        loginProgress?.visibility = if (show) View.VISIBLE else View.GONE
        loginProgress?.animate()
            ?.setDuration(shortAnimTime)
            ?.alpha((if (show) 1 else 0).toFloat())
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    loginProgress?.visibility = if (show) View.VISIBLE else View.GONE
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
                        images_recyclerview?.findViewHolderForAdapterPosition(MainActivity.currentPosition)
                    if (selectedViewHolder ==  null) return


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
        Timber.i("onImage clicked: position: $adapterPosition")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gotoImageDetailsTransition(view)
        } else {
            gotoImageDetails()
        }
    }

    override fun onImageLongClicked(imageUrl: String?) {
        imageUrl?.let { startBluetoothAndSend(activity, it) }
    }

    /** Ask user to enable Bluetooth
     * Put extra bundle in the Intent with data to receive onActivityResult */
    private fun startBluetoothAndSend(activity: Activity?, imageUrl: String?) {
        if (activity == null) return
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return
        } else {
            if (mBluetoothAdapter.isEnabled.not()) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBtIntent.putExtra("URL", imageUrl)
                val bundle = Bundle()
                bundle.putString("URL", imageUrl)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT, bundle)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //BT activated by user
                sendViaBluetooth(data?.getStringExtra("URL"))
            }
        } else {
            //nothing
        }
    }

    private fun sendViaBluetooth(imageUrl: String?) {
        if (imageUrl == null) return

        val values = ContentValues()
        val btDevice: BluetoothDevice? = getDeviceList()
        val address = btDevice?.address

        //values.put(BluetoothShare.URI, Uri.fromFile(File("somefile.mp3")).toString())
        values.put(BluetoothShare.URI, imageUrl)
        values.put(BluetoothShare.DESTINATION, address)
        values.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND)
        val timeStamp = System.currentTimeMillis()
        values.put(BluetoothShare.TIMESTAMP, timeStamp)
        val contentUri = activity?.contentResolver?.insert(BluetoothShare.CONTENT_URI, values)

    }

    private fun getDeviceList(): BluetoothDevice? {
        val mBlurAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices: MutableSet<BluetoothDevice> = mBlurAdapter.bondedDevices
        if (pairedDevices.isEmpty()) {
            Timber.e("No paired devices found")
            return null
        }
        for (pairedDevice in pairedDevices) {
            Timber.d("DeviceActivity Device : address : ${pairedDevice.address} name :${pairedDevice.name}")
        }
        return pairedDevices.first()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gotoImageDetailsTransition(view: View) {
        (exitTransition as TransitionSet).excludeTarget(view, true)

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

    private fun gotoImageDetails() {
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.container, DetailsViewPagerFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        private const val REQUEST_ENABLE_BT: Int = 1
    }

}