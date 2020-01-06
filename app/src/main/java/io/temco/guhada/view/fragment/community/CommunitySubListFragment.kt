package io.temco.guhada.view.fragment.community

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.enum.CommunityOrderType
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.viewmodel.CommunitySubListViewModel
import io.temco.guhada.databinding.FragmentCommunitySubListBinding
import io.temco.guhada.view.activity.CommunityDetailActivity
import io.temco.guhada.view.adapter.CommunityBoardAdapter
import io.temco.guhada.view.fragment.ListBottomSheetFragment
import io.temco.guhada.view.fragment.base.BaseFragment


/**
 * @author park jungho
 *
 * 커뮤니티 공통 리스트 fragment
 * @author Hyeyeon Park
 * @since 2019.08.21
 */
class CommunitySubListFragment : BaseFragment<FragmentCommunitySubListBinding>(), SwipeRefreshLayout.OnRefreshListener {
    enum class CommunityListType(val type: String) {
        TEXT("TEXT"), IMAGE("IMAGE")
    }

    var linearlayoutCommunitylistFilter1Index = -1
    var linearlayoutCommunitylistFilter2Index = 0

    lateinit var info: CommunityInfo
    private lateinit var mViewModel: CommunitySubListViewModel

    override fun getBaseTag(): String = CommunitySubListFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_community_sub_list

    override fun init() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        initViewModel()
        initFilters()

        mViewModel.getCommunityList()
        mBinding.executePendingBindings()
    }

    override fun onRefresh() {
        mBinding.swipeRefreshLayout.isRefreshing = false
        mViewModel.mPage = 0
        mViewModel.getCommunityList()
    }

    private fun initViewModel() {
        mViewModel = CommunitySubListViewModel()
        mViewModel.mCommunityInfo = info
        mViewModel.mCommunityResponse.observe(this, Observer {
            if (it.bbs.isNotEmpty()) {
                initList(it.bbs)
                mBinding.linearlayoutCommunitylistMore.visibility = if (it.bbs.size < it.totalCount) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            mBinding.recyclerviewCommunityist.visibility = if (it.bbs.isEmpty()) View.GONE else View.VISIBLE
            mViewModel.mEmptyViewVisible = ObservableBoolean(it.bbs.isEmpty())
            mViewModel.notifyPropertyChanged(BR.mEmptyViewVisible)
        })

        mViewModel.mRedirectDetailTask = {
            val intent = Intent(context, CommunityDetailActivity::class.java)
            intent.putExtra("bbsId", it.bbsId)
            if ("".equals(mViewModel.mCommunityInfo.communityName) && mViewModel.mCommunityInfo.communityCategoryId == 0) {
                val frag = this.parentFragment as CommunityMainFragment
                if (frag.mViewModel.communityInfoList?.value ?: null != null) {
                    loop@ for (data in frag.mViewModel.communityInfoList.value!!) {
                        if (it.categoryId.toInt() == data.communityCategoryId) {
                            intent.putExtra("info", data)
                            break@loop
                        }
                    }
                } else {
                    intent.putExtra("info", mViewModel.mCommunityInfo)
                }
            } else {
                intent.putExtra("info", mViewModel.mCommunityInfo)
            }
            intent.putExtra("filterId", mViewModel.mFilterId)
            intent.putExtra("order", mViewModel.mOrder)
            intent.putExtra("categoryId", mViewModel.mCommunityInfo.communityCategoryId.toLong())
            (context as Activity).startActivityForResult(intent, Flag.RequestCode.COMMUNITY_DETAIL)
        }
        mBinding.viewModel = mViewModel
    }

    private fun initFilters() {
        for (item in info.communityCategorySub.categoryFilterList)
            mViewModel.mCategoryFilterList.add(item.name)

        // 말머리 필터
        mBinding.linearlayoutCommunitylistFilter1.setOnClickListener {
            val bottomSheet = ListBottomSheetFragment(mBinding.root.context).apply {
                this.mList = mViewModel.mCategoryFilterList
                this.mTitle = mBinding.root.context.getString(R.string.community_filter_title1)
                this.selectedIndex = linearlayoutCommunitylistFilter1Index
                this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                    override fun onItemClick(position: Int) {
                        linearlayoutCommunitylistFilter1Index = position
                        val selectedCategory = mViewModel.mCommunityInfo.communityCategorySub.categoryFilterList[position]
                        mBinding.textviewCommunitylistFilter1.text = selectedCategory.name
                        mViewModel.mPage = 0
                        mViewModel.mFilterId = selectedCategory.id
                        mViewModel.getCommunityList()
                    }

                    override fun onClickClose() {
                        this@apply.dismiss()
                    }
                }
            }

            if (fragmentManager != null) bottomSheet.show(fragmentManager!!, baseTag)
        }

        // 정렬  필터
        mBinding.linearlayoutCommunitylistFilter2.setOnClickListener {
            val bottomSheet = ListBottomSheetFragment(mBinding.root.context).apply {
                this.mList = mViewModel.mSortFilterList
                this.mTitle = mBinding.root.context.getString(R.string.community_filter_title2)
                this.selectedIndex = linearlayoutCommunitylistFilter2Index
                this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                    override fun onItemClick(position: Int) {
                        linearlayoutCommunitylistFilter2Index = position
                        val selectedSort = mViewModel.mSortFilterList[position]
                        mBinding.textviewCommunitylistFilter2.text = selectedSort
                        mViewModel.mOrder = when (selectedSort) {
                            CommunityOrderType.DATE_DESC.label -> CommunityOrderType.DATE_DESC.type
                            CommunityOrderType.VIEW_DESC.label -> CommunityOrderType.VIEW_DESC.type
                            CommunityOrderType.LIKE_DESC.label -> CommunityOrderType.LIKE_DESC.type
                            CommunityOrderType.COMMENT_DESC.label -> CommunityOrderType.COMMENT_DESC.type
                            else -> CommunityOrderType.DATE_DESC.type
                        }
                        mViewModel.mPage = 0
                        mViewModel.getCommunityList()
                    }

                    override fun onClickClose() {
                        this@apply.dismiss()
                    }
                }
            }

            if (fragmentManager != null) bottomSheet.show(fragmentManager!!, baseTag)
        }
    }

    private fun initList(bbs: MutableList<CommunityBoard>) {
        val SPAN_COUNT = 2
        val listType = info.communityCategorySub.type
        when (listType) {
            CommunityListType.IMAGE.type -> mBinding.recyclerviewCommunityist.layoutManager = GridLayoutManager(context, SPAN_COUNT)
            else -> mBinding.recyclerviewCommunityist.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        if (mBinding.recyclerviewCommunityist.adapter == null)
            mBinding.recyclerviewCommunityist.adapter = CommunityBoardAdapter(listType).apply {
                this.mList = bbs
                this.mViewModel = this@CommunitySubListFragment.mViewModel
            }
        else {
            (mBinding.recyclerviewCommunityist.adapter as CommunityBoardAdapter).mList = bbs
            (mBinding.recyclerviewCommunityist.adapter as CommunityBoardAdapter).notifyDataSetChanged()
        }
        mBinding.executePendingBindings()
    }

}