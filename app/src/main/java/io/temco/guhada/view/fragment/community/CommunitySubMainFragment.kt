package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.databinding.FragmentCommunitySubMainBinding
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * 19.08.19
 * @author park jungho
 *
 * 메인
 *
 *
 */
class CommunitySubMainFragment(val info : CommunityInfo) : BaseFragment<FragmentCommunitySubMainBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return CommunitySubMainFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_main
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