package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentCommunitySubFashiontalkBinding
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * 19.08.19
 * @author park jungho
 *
 * 패션토크
 *
 *
 */
class CommunitySubFashionTalkFragment : BaseFragment<FragmentCommunitySubFashiontalkBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return CommunitySubFashionTalkFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_fashiontalk
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