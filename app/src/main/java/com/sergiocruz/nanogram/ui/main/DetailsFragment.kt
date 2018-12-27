package com.sergiocruz.nanogram.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.adapter.ViewPagerAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterFullScreen()
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
            override fun onPageSelected(position: Int) = view_pager.setCurrentItem(position, true)
        })
        val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                listIndex = position
            }
        }
        view_pager.addOnPageChangeListener(onPageChangeListener)
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
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    private fun exitFullScreen() {
        val decorView = activity?.window?.decorView
        decorView?.findViewById<Toolbar>(R.id.action_bar)?.visibility = VISIBLE
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}

