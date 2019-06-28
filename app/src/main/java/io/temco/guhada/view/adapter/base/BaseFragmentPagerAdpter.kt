package io.temco.guhada.view.adapter.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BaseFragmentPagerAdpter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment) {
        this.fragments.add(fragment)
    }
}