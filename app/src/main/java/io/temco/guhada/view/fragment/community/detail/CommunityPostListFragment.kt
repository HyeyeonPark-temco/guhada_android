package io.temco.guhada.view.fragment.community.detail

import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommentBinding
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * @author park jungho
 * 19.12.10
 * 커뮤니티 상세에 게시글 목록
 */
class CommunityPostListFragment(val viewModel : CommunityDetailViewModel) : BaseFragment<FragmentCommentBinding>() {
    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityPostListFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_community_list
    override fun init() {
        mBinding.viewModel = viewModel

        /*if (mBinding.recyclerviewCommentList.adapter == null) {
            viewModel.commentAdapter = CommentListAdapter(viewModel).apply { mList = viewModel.commentList.value!! }
            mBinding.recyclerviewCommentList.adapter = viewModel.commentAdapter
            mBinding.executePendingBindings()
        }*/
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