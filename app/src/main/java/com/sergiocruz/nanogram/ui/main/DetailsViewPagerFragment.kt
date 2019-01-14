package com.sergiocruz.nanogram.ui.main

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
import androidx.transition.TransitionInflater
import androidx.viewpager.widget.ViewPager
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.adapter.ImagePagerAdapter
import com.sergiocruz.nanogram.util.enterFullScreen
import kotlinx.android.synthetic.main.fragment_viewpager.*
import timber.log.Timber

class DetailsViewPagerFragment : Fragment() {
    companion object {
        private const val ARG_ITEM_INDEX = "item_index"
        fun newInstance(index: Int): DetailsViewPagerFragment {
            val arguments = Bundle()
            arguments.putInt(ARG_ITEM_INDEX, index)
            val detailFragment = DetailsViewPagerFragment()
            detailFragment.arguments = arguments
            return detailFragment
        }
    }

    private var listIndex: Int = 0
    private lateinit var viewModel: MainViewModel
    private lateinit var imagePagerAdapter: ImagePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (arguments != null && arguments!!.containsKey(ARG_ITEM_INDEX)) {
            listIndex = arguments!!.getInt(ARG_ITEM_INDEX)
        }

        return inflater.inflate(R.layout.fragment_viewpager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.getUserMedia(this.context!!).observe(this, Observer {
            setupViewPager()
            imagePagerAdapter.setSize(it.size)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                prepareEnterSharedElementTransition()
                // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
                if (savedInstanceState == null) {
                    postponeEnterTransition()
                }
            }
            view_pager.setCurrentItem(listIndex, false)
        })
    }

    private fun setupViewPager() {
        imagePagerAdapter = ImagePagerAdapter(this, listIndex)
        view_pager.adapter = imagePagerAdapter
        view_pager.offscreenPageLimit = 1
        // Transformation animation on page switch
        view_pager.setPageTransformer(true, ZoomOutPageTransformer())
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                listIndex = position
                MainActivity.currentPosition = position
            }
        })
    }

    /** Keep the same image on screen rotation */
    override fun onPause() {
        super.onPause()
        val arguments = Bundle()
        arguments.putInt(ARG_ITEM_INDEX, listIndex)
        /** will replace current arguments */
        this.arguments = arguments
    }

    /** Prepares the shared element transition from and back to the grid fragment.*/
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun prepareEnterSharedElementTransition() {
        sharedElementEnterTransition = TransitionInflater.from(activity)
            .inflateTransition(R.transition.image_shared_element_transition)
            .setDuration(resources.getInteger(R.integer.transition_duration).toLong())

        // A similar mapping is set at the ArticleListFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onSharedElementStart(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                enterFullScreen(activity)
                super.onSharedElementStart(
                    sharedElementNames,
                    sharedElements,
                    sharedElementSnapshots
                )
            }
            override fun onMapSharedElements(
                names: List<String>,
                sharedElements: MutableMap<String, View>
            ) {
                // Locate the image view at the primary fragment (the ImageFragment that is currently
                // visible). To locate the fragment, call instantiateItem with the selection position.
                // At this stage, the method will simply return the fragment at the position and will
                // not create a new one.
                val currentFragment = view_pager.adapter?.instantiateItem(
                    view_pager,
                    MainActivity.currentPosition
                ) as Fragment
                val view = currentFragment.view ?: return

                // Map the first shared element name to the child ImageView.
                val name = view.findViewById<View>(R.id.imageview)
                sharedElements[names[0]] = name
                Timber.i(":On Map shared element name: ${name.transitionName}")
            }
        })
    }
}
