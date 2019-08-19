package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentCommunitySubSaleBinding
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * 19.08.19
 * @author park jungho
 *
 * 세일정보
 *
 *
 */
class CommunitySubSaleFragment : BaseFragment<FragmentCommunitySubSaleBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return CommunitySubSaleFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_sale
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