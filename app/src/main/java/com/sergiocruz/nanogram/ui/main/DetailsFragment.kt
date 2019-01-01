package com.sergiocruz.nanogram.ui.main

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.adapter.ViewPagerAdapter
import com.sergiocruz.nanogram.ui.main.MainActivity.Companion.currentPosition
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment : Fragment() {
    companion object {
        private const val ARG_ITEM_INDEX = "item_index"
        fun newInstance(index: Int): DetailsFragment {
            val arguments = Bundle()
            arguments.putInt(ARG_ITEM_INDEX, index)
            val detailFragment = DetailsFragment()
            detailFragment.arguments = arguments
            return detailFragment
        }
    }

    private var listIndex: Int = 0
    private lateinit var viewModel: MainViewModel
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterFullScreen()

        arguments.let {
            if (it!!.containsKey(ARG_ITEM_INDEX)) {
                listIndex = arguments!!.getInt(ARG_ITEM_INDEX)
            }
        }
        return inflater.inflate(R.layout.details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.getUserMedia(this.context!!).observe(this, Observer {
            setupViewPager()
            viewPagerAdapter.swap(it.size)
        })
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(this, listIndex)
        view_pager.adapter = viewPagerAdapter
        // Transformation animation on page switch
        view_pager.setPageTransformer(true, ZoomOutPageTransformer())
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                listIndex = position
                currentPosition = position
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) prepareEnterSharedElementTransition()
    }

    /** Keep the same image on screen rotation */
    override fun onPause() {
        super.onPause()
        val arguments = Bundle()
        arguments.putInt(ARG_ITEM_INDEX, listIndex)
        this.arguments = arguments
    }

    override fun onResume() {
        super.onResume()
        view_pager.setCurrentItem(listIndex, true)
    }

    override fun onDetach() {
        super.onDetach()
        exitFullScreen()
    }

    private fun enterFullScreen() {
        val decorView = activity?.window?.decorView
        decorView?.findViewById<Toolbar>(R.id.action_bar)?.visibility = GONE
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    }

    private fun exitFullScreen() {
        val decorView = activity?.window?.decorView
        decorView?.findViewById<Toolbar>(R.id.action_bar)?.visibility = VISIBLE
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /** Prepares the shared element transition from and back to the grid fragment.*/
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun prepareEnterSharedElementTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)

        // A similar mapping is set at the ArticleListFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                // Locate the image view at the primary fragment (the ImageFragment that is currently
                // visible). To locate the fragment, call instantiateItem with the selection position.
                // At this stage, the method will simply return the fragment at the position and will
                // not create a new one.
                val currentFragment = view_pager.adapter?.instantiateItem(view_pager, MainActivity.currentPosition) as Fragment
                val view = currentFragment.view ?: return

                // Map the first shared element name to the child ImageView.
                sharedElements[names[0]] = view.findViewById(R.id.image_item)
            }
        })
    }
}

