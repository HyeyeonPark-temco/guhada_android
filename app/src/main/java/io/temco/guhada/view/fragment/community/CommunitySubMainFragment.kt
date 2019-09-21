package io.temco.guhada.view.fragment.community

import android.app.Activity
import android.content.Intent
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.databinding.FragmentCommunitySubMainBinding
import io.temco.guhada.view.activity.CommunityDetailActivity
import io.temco.guhada.view.fragment.base.BaseFragment
import android.os.Bundle




/**
 * 19.08.19
 * @author park jungho
 *
 * 메인
 *
 *
 */
class CommunitySubMainFragment : BaseFragment<FragmentCommunitySubMainBinding>() {

    // -------- LOCAL VALUE --------
    lateinit var info: CommunityInfo
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////


    override fun getBaseTag(): String = CommunitySubMainFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_community_sub_main
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