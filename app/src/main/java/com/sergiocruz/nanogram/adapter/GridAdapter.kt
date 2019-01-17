/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.ui.main.DetailsViewPagerFragment
import com.sergiocruz.nanogram.ui.main.MainActivity
import kotlinx.android.synthetic.main.item_image_layout.view.*
import java.util.concurrent.atomic.AtomicBoolean

class GridAdapter(fragment: Fragment) : RecyclerView.Adapter<GridAdapter.ImageViewHolder>() {
    private val viewHolderListener: ViewHolderListener

    init {
        this.viewHolderListener = ViewHolderListenerImpl(fragment)
    }

    private var imageList: MutableList<ImageVar>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_layout, parent, false)
        return ImageViewHolder(view, viewHolderListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, adapterPosition: Int) {

        val url = imageList?.get(adapterPosition)?.images?.standardResolution?.url

        // Set the string value of the imageView resource as the unique transition name for the view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.imageView.transitionName = url.hashCode().toString()
        }

        Glide.with(holder.itemView.context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any,
                    target: Target<Drawable>, isFirstResource: Boolean
                ): Boolean {
                    viewHolderListener.onLoadCompleted(holder.imageView, adapterPosition)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolderListener.onLoadCompleted(holder.imageView, adapterPosition)
                    return false
                }
            })
            .into(holder.imageView)


    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    fun swap(data: MutableList<ImageVar>) {
        data.let {
            this.imageList = data
            notifyDataSetChanged()
        }
    }

    /**
     * A listener that is attached to all ViewHolders to handle imageView loading events and clicks.
     */
    interface ViewHolderListener {
        fun onLoadCompleted(view: ImageView, adapterPosition: Int)
        fun onItemClicked(view: View, adapterPosition: Int)
    }

    /**
     * Default [implementation.][ViewHolderListener]
     */
    private class ViewHolderListenerImpl internal constructor(private val fragment: Fragment) :
        ViewHolderListener {
        private val enterTransitionStarted: AtomicBoolean = AtomicBoolean()

        override fun onLoadCompleted(view: ImageView, adapterPosition: Int) {
            // Call startPostponedEnterTransition only when the 'selected' imageView loading is completed.
            if (MainActivity.currentPosition != adapterPosition) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }

            fragment.startPostponedEnterTransition()
        }

        /**
         * Handles a view click by setting the current position to the given `position` and
         * starting a [DetailsViewPagerFragment] which displays the imageView at the position.
         *
         * @param view the clicked [ImageView] (the shared element view will be re-mapped at the
         * GridFragment's SharedElementCallback)
         * @param adapterPosition the selected view position
         */
        override fun onItemClicked(view: View, adapterPosition: Int) {
            // Update the position.
            MainActivity.currentPosition = adapterPosition

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).

            val transaction = fragment.fragmentManager!!.beginTransaction()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                (fragment.exitTransition as TransitionSet).excludeTarget(view, true)

                val transitioningView = view.item_image
                transaction
                    .setReorderingAllowed(true) // Optimize for shared element transition
                    .addSharedElement(transitioningView, transitioningView.transitionName)
                    .replace(
                        R.id.container,
                        DetailsViewPagerFragment(),
                        DetailsViewPagerFragment::class.java.simpleName
                    )
                    .addToBackStack(null)
                    .commit()
            } else {
                transaction
                    .replace(
                        R.id.container,
                        DetailsViewPagerFragment(),
                        DetailsViewPagerFragment::class.java.simpleName
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    /** ViewHolder for the grid's images. */
    class ImageViewHolder(
        itemView: View,
        private val viewHolderListener: GridAdapter.ViewHolderListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        internal val imageView: ImageView = itemView.item_image
        internal val caption: TextView = itemView.caption
        internal val likes: TextView = itemView.likes
        internal val comments: TextView = itemView.comments

        override fun onClick(view: View) {
            // Let the listener start the ImagePagerFragment.
            viewHolderListener.onItemClicked(view, adapterPosition)
        }
    }

}