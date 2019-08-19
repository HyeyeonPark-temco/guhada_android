package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentCommunitySubFashionrcmdBinding
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * 19.08.19
 * @author park jungho
 *
 * 패션추천
 *
 *
 */
class CommunitySubFashionRcmdFragment : BaseFragment<FragmentCommunitySubFashionrcmdBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return CommunitySubFashionRcmdFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_fashionrcmd
    }

    override fun init() {

    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////


}