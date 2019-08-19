package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentCommunitySubNotiBinding
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * 19.08.19
 * @author park jungho
 *
 * 공지사항
 *
 *
 */
class CommunitySubNotiFragment : BaseFragment<FragmentCommunitySubNotiBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return CommunitySubNotiFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_noti
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