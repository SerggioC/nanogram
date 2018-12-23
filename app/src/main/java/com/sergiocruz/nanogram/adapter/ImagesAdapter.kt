package com.sergiocruz.nanogram.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.util.animateItemViewSlideFromBottom
import kotlinx.android.synthetic.main.image_item_layout.view.*

class ImagesAdapter(private val imageClickListener: ImageClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var imageList: MutableList<ImageVar>? = null

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    fun swapData(data: MutableList<ImageVar>) {
        data.let {
            this.imageList = data
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_layout, parent, false)
        return ItemImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image: ImageVar? = imageList?.get(position)
        holder as ItemImageViewHolder

        Glide.with(holder.imageView.context)
            .load(image?.images?.lowResolution?.url)
            .transition(withCrossFade())
            .apply(
                RequestOptions()
                    .error(R.mipmap.ic_launcher)
                    .diskCacheStrategy(AUTOMATIC)
                    .centerCrop()
            )
            .into(holder.imageView)

        holder.caption.text = image?.caption?.text
        holder.likes.text = image?.likes?.count.toString()
        holder.comments.text = image?.comments?.count.toString()

        animateItemViewSlideFromBottom(holder.itemView, (50 * position).toLong())
    }

    interface ImageClickListener {
        fun onImageClicked(image: ImageVar?)
    }

    inner class ItemImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val imageView: ImageView = itemView.image_item
        internal val caption: TextView = itemView.caption
        internal val likes: TextView = itemView.likes
        internal val comments: TextView = itemView.comments

        init {
            imageView.setOnClickListener {
                imageClickListener.onImageClicked(imageList?.get(adapterPosition))
            }
        }
    }

}