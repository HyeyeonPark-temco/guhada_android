package io.temco.guhada.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.model.community.CommunityType
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.community.CommunitySubListFragment
import io.temco.guhada.view.fragment.community.CommunitySubMainFragment
import io.temco.guhada.view.fragment.community.CommunitySubNotiFragment
import io.temco.guhada.view.fragment.community.CommunitySubPopularFragment
import java.util.*

class CommunityPagerAdapter(private val mFragmentManager: FragmentManager, val tabInfo : ArrayList<CommunityInfo>) : FragmentPagerAdapter(mFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val TAG = CommunityPagerAdapter::class.java.simpleName

    var customLayoutMap: WeakHashMap<Int, BaseFragment<*>> = WeakHashMap()


    override fun getItem(position: Int): Fragment {
        var frg : BaseFragment<*>? = null
        when(tabInfo.get(position).type){
            CommunityType.MAIN-> frg = CommunitySubMainFragment(tabInfo.get(position))
            CommunityType.POPULAR-> frg = CommunitySubPopularFragment(tabInfo.get(position))
            CommunityType.NOTIFICATION-> frg = CommunitySubNotiFragment(tabInfo.get(position))
            else ->  frg = CommunitySubListFragment(tabInfo.get(position))
        }
        frg?.let {customLayoutMap.put(position, it) }
        return frg?: Fragment()
    }

    override fun getCount() = tabInfo.size


}