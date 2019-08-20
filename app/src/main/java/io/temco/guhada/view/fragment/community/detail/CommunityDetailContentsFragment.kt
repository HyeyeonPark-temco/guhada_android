package io.temco.guhada.view.fragment.community.detail

import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommunityDetailContentBinding
import io.temco.guhada.view.fragment.base.BaseFragment

class CommunityDetailContentsFragment(val viewModel : CommunityDetailViewModel) : BaseFragment<FragmentCommunityDetailContentBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityDetailContentsFragment::class.java!!.getSimpleName()
    override fun getLayoutId(): Int = R.layout.fragment_community_detail_content
    override fun init() {
        mBinding.viewModel = viewModel

        setDetailView()
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun setDetailView(){

    }

    ////////////////////////////////////////////////


}
