package com.sergiocruz.nanogram.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sergiocruz.nanogram.ui.main.ImageFragment
import java.lang.ref.WeakReference

open class ImagePagerAdapter(fragment: Fragment, private var index: Int) :
    FragmentStatePagerAdapter(fragment.childFragmentManager) {

    private var weakData: WeakReference<Int>? = null

    override fun getCount(): Int {
        return weakData?.get() ?: 0
    }

    fun swap(data: Int) {
        weakData = WeakReference(data)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(position)
    }

}