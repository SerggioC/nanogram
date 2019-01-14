package com.sergiocruz.nanogram.adapter

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.ui.main.MainActivity
import com.sergiocruz.nanogram.util.SlideItemsFromBottom
import kotlinx.android.synthetic.main.item_image_layout.view.*
import java.util.concurrent.atomic.AtomicBoolean

class GridImageAdapter(
    private val imageClickListener: ImageClickListener,
    private val gridFragment: Fragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var imageList: MutableList<ImageVar>? = null
    private val enterTransitionStarted = AtomicBoolean()

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    fun swap(data: MutableList<ImageVar>) {
        data.let {
            if (this.imageList != data) {
                this.imageList = data
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_layout, parent, false)
        return ItemImageViewHolder(view)
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image: ImageVar? = imageList?.get(position)
        holder as ItemImageViewHolder

        val url = image?.images?.standardResolution?.url

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.imageView.transitionName = url.hashCode().toString()
        }

        Glide.with(holder.imageView.context)
            .load(url)
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (MainActivity.currentPosition != position) {
                        return false
                    }
                    if (enterTransitionStarted.getAndSet(true)) {
                        return false
                    }
                    gridFragment.startPostponedEnterTransition()
                    return false
                }
            })
            .transition(withCrossFade(400))
            .apply(
                RequestOptions()
//                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .error(R.mipmap.ic_launcher)
            )
            .into(holder.imageView)

        holder.caption.text = image?.caption?.text
        holder.likes.text = image?.likes?.count.toString()
        holder.comments.text = image?.comments?.count.toString()

        SlideItemsFromBottom(holder.itemView, (20 * position).toLong())

    }

    interface ImageClickListener {
        fun onImageClicked(
            adapterPosition: Int,
            view: View
        )
    }

    inner class ItemImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val imageView: ImageView = itemView.item_image
        internal val caption: TextView = itemView.caption
        internal val likes: TextView = itemView.likes
        internal val comments: TextView = itemView.comments

        init {
            imageView.setOnClickListener {
                imageClickListener.onImageClicked(adapterPosition, it)
            }
        }
    }

}