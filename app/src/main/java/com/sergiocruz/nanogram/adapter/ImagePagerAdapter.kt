package com.sergiocruz.nanogram.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sergiocruz.nanogram.ui.main.ImageFragment

open class ImagePagerAdapter(fragment: Fragment, private var index: Int) :
    FragmentStatePagerAdapter(fragment.childFragmentManager) {

    private var dataSize: Int? = null

    override fun getCount(): Int {
        return dataSize ?: 0
    }

    fun setSize(size: Int) {
        dataSize = size
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(position)
    }

}