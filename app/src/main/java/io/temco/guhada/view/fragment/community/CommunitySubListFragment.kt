package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.databinding.FragmentCommunitySubListBinding
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * @author park jungho
 *
 * 커뮤니티 공통 리스트 fragment
 */
class CommunitySubListFragment(val info : CommunityInfo) : BaseFragment<FragmentCommunitySubListBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return CommunitySubListFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_list
    }

    override fun init() {
        if(CustomLog.flag) CustomLog.L(getBaseTag(),info.toString())

    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////


}