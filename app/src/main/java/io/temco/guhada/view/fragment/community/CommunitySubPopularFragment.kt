package io.temco.guhada.view.fragment.community

import android.os.Bundle
import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.databinding.FragmentCommunitySubPopularBinding
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * 19.08.19
 * @author park jungho
 *
 * 인기글
 *
 *
 */
class CommunitySubPopularFragment : BaseFragment<FragmentCommunitySubPopularBinding>() {

    // -------- LOCAL VALUE --------
    lateinit var info: CommunityInfo
    // -----------------------------


    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////


    override fun getBaseTag(): String {
        return CommunitySubPopularFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_popular
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