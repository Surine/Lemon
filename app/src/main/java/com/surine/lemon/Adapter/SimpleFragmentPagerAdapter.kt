package com.surine.lemon.Adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by surine on 17-10-1.
 * ViewPager适配器
 */

class SimpleFragmentPagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>, private val titles: List<String>) : FragmentPagerAdapter(fm) {
    private val context: Context? = null

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}
