package com.sergiocruz.nanogram.ui.main

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.util.enterFullScreen
import kotlinx.android.synthetic.main.fragment_image.*


class ImageFragment : Fragment() {
    private var listIndex: Int = -1

    companion object {
        private const val ARG_ITEM_INDEX = "item_index"
        fun newInstance(index: Int): ImageFragment {
            val arguments = Bundle()
            arguments.putInt(ARG_ITEM_INDEX, index)
            val imageFragment = ImageFragment()
            imageFragment.arguments = arguments
            return imageFragment
        }
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments.let {
            if (it!!.containsKey(ARG_ITEM_INDEX)) {
                listIndex = arguments!!.getInt(ARG_ITEM_INDEX)
            }
        }
        savedInstanceState?.let { enterFullScreen(activity) }
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        setupViews(viewModel.getImageVarForIndex(listIndex, this.context!!))
    }

    // Can be used with databinding in the XML
    private fun setupViews(imageVar: ImageVar?) {
        likes.text = imageVar?.likes?.count?.toString() ?: ""
        comments.text = imageVar?.comments?.count?.toString() ?: ""
        caption.text = imageVar?.caption?.text ?: ""

        val url = imageVar?.images?.standardResolution?.url

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageview.transitionName = url.hashCode().toString()
        }

        Glide.with(this.context!!)
            .load(url)
            .listener(object : RequestListener<Drawable?> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    parentFragment?.startPostponedEnterTransition()
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    parentFragment?.startPostponedEnterTransition()
                    return false
                }
            })
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .error(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            )
            .into(imageview)

        root_item_layout.setOnClickListener {
            likes.toggle()
            comments.toggle()
            caption.toggle()
        }
    }

    private fun View.toggle() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }


}