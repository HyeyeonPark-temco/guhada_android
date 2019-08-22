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

    override fun getBaseTag(): String = CommunitySubMainFragment::class.java!!.getSimpleName()
    override fun getLayoutId(): Int = R.layout.fragment_community_sub_main
    override fun init() {
        if(CustomLog.flag) CustomLog.L(getBaseTag(),info.toString())
        mBinding.setClickListener {
            var intent = Intent(context as Activity, CommunityDetailActivity::class.java)
            intent.putExtra("bbsId", 282286L)// 282305L 282287L 282286L 282286L
            intent.putExtra("info", info)
            (context as Activity).startActivityForResult(intent, Flag.RequestCode.COMMUNITY_DETAIL)
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////


}