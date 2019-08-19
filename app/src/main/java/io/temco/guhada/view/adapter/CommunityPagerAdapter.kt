package io.temco.guhada.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.community.*
import java.util.*

class CommunityPagerAdapter(private val mFragmentManager: FragmentManager, val itemCnt : Int) : FragmentPagerAdapter(mFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val TAG = CommunityPagerAdapter::class.java.simpleName

    var customLayoutMap: WeakHashMap<Int, BaseFragment<*>> = WeakHashMap()


    override fun getItem(position: Int): Fragment {
        var frg : BaseFragment<*>? = null
        when(position){
            0-> frg = CommunitySubMainFragment()
            1-> frg = CommunitySubPopularFragment()
            2-> frg = CommunitySubNotiFragment()
            3-> frg = CommunitySubFashionQnaFragment()
            4-> frg = CommunitySubFashionTalkFragment()
            5-> frg = CommunitySubFashionRcmdFragment()
            6-> frg = CommunitySubFashionUserFragment()
            7-> frg = CommunitySubCollectionFragment()
            8-> frg = CommunitySubFashionVideoFragment()
            9-> frg = CommunitySubPhotoFragment()
            10-> frg = CommunitySubRealFragment()
            11-> frg = CommunitySubReviewFragment()
            12-> frg = CommunitySubSaleFragment()
        }
        frg?.let {customLayoutMap.put(position, it) }
        return frg?: Fragment()
    }

    override fun getCount() = itemCnt


}