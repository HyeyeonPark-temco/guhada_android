package io.temco.guhada.view.fragment.community

import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentCommunitySubCollectBinding
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * 19.08.19
 * @author park jungho
 *
 * 소장품
 *
 *
 */
class CommunitySubCollectionFragment : BaseFragment<FragmentCommunitySubCollectBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return CommunitySubCollectionFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_collect
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