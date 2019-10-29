package io.temco.guhada.view.adapter.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class BaseFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private val fragments: MutableList<Fragment> = mutableListOf()
    var mTabTitles = mutableListOf<String>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment) {
        this.fragments.add(fragment)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if(mTabTitles.isEmpty()) super.getPageTitle(position) else mTabTitles[position]
    }
}