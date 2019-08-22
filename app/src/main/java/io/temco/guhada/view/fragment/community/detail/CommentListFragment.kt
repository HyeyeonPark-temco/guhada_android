package io.temco.guhada.view.fragment.community.detail

import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommentBinding
import io.temco.guhada.view.adapter.CommentListAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

class CommentListFragment(val viewModel : CommunityDetailViewModel) : BaseFragment<FragmentCommentBinding>() {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommentListFragment::class.java!!.getSimpleName()
    override fun getLayoutId(): Int = R.layout.fragment_comment
    override fun init() {
        mBinding.viewModel = viewModel

        if (mBinding.recyclerviewCommentList.adapter == null) {
            viewModel.commentAdapter = CommentListAdapter(viewModel).apply { mList = viewModel.commentList.value!! }
            mBinding.recyclerviewCommentList.adapter = viewModel.commentAdapter
            mBinding.executePendingBindings()
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////



    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////


}