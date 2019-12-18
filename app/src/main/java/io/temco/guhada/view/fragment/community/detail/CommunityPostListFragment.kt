package io.temco.guhada.view.fragment.community.detail

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommunityListBinding
import io.temco.guhada.view.adapter.CommunityPostListAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * @author park jungho
 * 19.12.10
 * 커뮤니티 상세에 게시글 목록
 */
class CommunityPostListFragment(val viewModel : CommunityDetailViewModel) : BaseFragment<FragmentCommunityListBinding>() {
    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityPostListFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_community_list
    override fun init() {
        mBinding.viewModel = viewModel
        mRedirectPostListObserverTask(viewModel.mCommunityResponse.value!!)
        mBinding.setClickMoreListener {
            if(mBinding.linearlayoutCommunitylistMore.visibility == View.VISIBLE){
                mBinding.linearlayoutCommunitylistMore.visibility = View.INVISIBLE
                viewModel.repository.getCommunityList()
            }
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    var mRedirectPostListObserverTask: (value: CommunityBoard.CommunityResponse) -> Unit = {
        if(CustomLog.flag) CustomLog.L("mCommunityResponse","mRedirectPostListObserverTask",it)
        initList(it.bbs)
        mBinding.linearlayoutCommunitylistMore.visibility = if (it.bbs.size < it.totalCount) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
    ////////////////////////////////////////////////



    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun initList(bbs: MutableList<CommunityBoard>) {
        val listType = viewModel.info.communityCategorySub.type
        var index = mBinding.recyclerviewCommunityList.adapter?.itemCount ?: 0
        if(CustomLog.flag) CustomLog.L("mCommunityResponse","initList index",index)
        mBinding.recyclerviewCommunityList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        if (mBinding.recyclerviewCommunityList.adapter == null) {
            mBinding.recyclerviewCommunityList.adapter = CommunityPostListAdapter(listType).apply {
                this.mList = bbs
                this.mViewModel = viewModel
            }
            (mBinding.recyclerviewCommunityList.adapter as CommunityPostListAdapter).notifyDataSetChanged()
        }else {
            (mBinding.recyclerviewCommunityList.adapter as CommunityPostListAdapter).mList = bbs
            (mBinding.recyclerviewCommunityList.adapter as CommunityPostListAdapter).notifyDataSetChanged()
        }
        mBinding.executePendingBindings()
    }


    ////////////////////////////////////////////////


}