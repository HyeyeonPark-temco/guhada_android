package io.temco.guhada.view.fragment.community

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.viewmodel.CommunitySubListViewModel
import io.temco.guhada.databinding.FragmentCommunitySubListBinding
import io.temco.guhada.view.adapter.CommunityBoardAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * @author park jungho
 *
 * 커뮤니티 공통 리스트 fragment
 * @author Hyeyeon Park
 * @since 2019.08.21
 */
class CommunitySubListFragment(private val info: CommunityInfo) : BaseFragment<FragmentCommunitySubListBinding>() {
    private lateinit var mViewModel: CommunitySubListViewModel

    override fun getBaseTag(): String {
        return CommunitySubListFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_list
    }

    override fun init() {
        if (CustomLog.flag) CustomLog.L(getBaseTag(), info.communityCategorySub.toString())

        initViewModel()

        mViewModel.getCommunityList()
    }

    private fun initViewModel() {
        mViewModel = CommunitySubListViewModel()
        mViewModel.mCommunityInfo = info
        mViewModel.mCommunityResponse.observe(this, Observer {
            initList(it.bbs)
        })
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initList(bbs: MutableList<CommunityBoard>) {
        val SPAN_COUNT = 2
        val listType = info.communityCategorySub.type
        when (listType) {
            CommunityListType.IMAGE.type -> mBinding.recyclerviewCommunityist.layoutManager = GridLayoutManager(context, SPAN_COUNT)
            else -> mBinding.recyclerviewCommunityist.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        if (mBinding.recyclerviewCommunityist.adapter == null)
            mBinding.recyclerviewCommunityist.adapter = CommunityBoardAdapter(listType).apply { this.mList = bbs }
        else {
            (mBinding.recyclerviewCommunityist.adapter as CommunityBoardAdapter).mList = bbs
            (mBinding.recyclerviewCommunityist.adapter as CommunityBoardAdapter).notifyDataSetChanged()
        }
        mBinding.executePendingBindings()
    }

    enum class CommunityListType(val type: String) {
        TEXT("TEXT"), IMAGE("IMAGE")
    }
}