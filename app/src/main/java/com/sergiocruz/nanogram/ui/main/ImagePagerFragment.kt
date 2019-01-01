package com.sergiocruz.nanogram.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.ImageVar
import kotlinx.android.synthetic.main.image_item_layout.*

class ImagePagerFragment : Fragment() {
    private var listIndex: Int = -1

    companion object {
        private const val ARG_ITEM_INDEX = "item_index"
        fun newInstance(index: Int): ImagePagerFragment {
            val arguments = Bundle()
            arguments.putInt(ARG_ITEM_INDEX, index)
            val detailFragment = ImagePagerFragment()
            detailFragment.arguments = arguments
            return detailFragment
        }
    }

    private lateinit var viewModel: MainViewModel

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
        // same layout as the item recyclerview just for testing and simplicity
        return inflater.inflate(R.layout.image_item_layout, container, false)
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

        val url = imageVar?.images?.thumbnail?.url
        Glide.with(this.context!!)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .error(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            )
            .into(image_item)

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