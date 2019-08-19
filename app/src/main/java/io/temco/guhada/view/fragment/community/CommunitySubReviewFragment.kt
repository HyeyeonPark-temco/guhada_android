package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentCommunitySubReviewBinding
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * 19.08.19
 * @author park jungho
 *
 * 쇼핑후기
 *
 *
 */
class CommunitySubReviewFragment : BaseFragment<FragmentCommunitySubReviewBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    protected override fun getBaseTag(): String {
        return CommunitySubReviewFragment::class.java!!.getSimpleName()
    }

    protected override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_review
    }

    protected override fun init() {

    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////


}