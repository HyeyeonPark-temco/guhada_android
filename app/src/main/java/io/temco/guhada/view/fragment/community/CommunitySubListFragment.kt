package io.temco.guhada.view.fragment.community

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.enum.CommunityOrderType
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.viewmodel.CommunitySubListViewModel
import io.temco.guhada.databinding.FragmentCommunitySubListBinding
import io.temco.guhada.view.CommunitySpinnerAdapter
import io.temco.guhada.view.activity.CommunityDetailActivity
import io.temco.guhada.view.adapter.CommunityBoardAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * @author park jungho
 *
 * 커뮤니티 공통 리스트 fragment
 * @author Hyeyeon Park
 * @since 2019.08.21
 */
class CommunitySubListFragment : BaseFragment<FragmentCommunitySubListBinding>(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var info: CommunityInfo

    private lateinit var mViewModel: CommunitySubListViewModel

    override fun getBaseTag(): String {
        return CommunitySubListFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_community_sub_list
    }

    override fun init() {
        if (CustomLog.flag) CustomLog.L(getBaseTag(), info.communityCategorySub.toString())

        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        initViewModel()
        initSpinner()

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
            if(it.bbs.isNotEmpty()){
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
        mViewModel.redirectDetailTask = {
            val intent = Intent(context, CommunityDetailActivity::class.java)
            intent.putExtra("bbsId", it.bbsId)
            intent.putExtra("info", mViewModel.mCommunityInfo)
            (context as Activity).startActivityForResult(intent, Flag.RequestCode.COMMUNITY_DETAIL)
        }
        mBinding.viewModel = mViewModel
    }

    private fun initSpinner() {
        for (item in info.communityCategorySub.categoryFilterList) {
            mViewModel.mCategoryFilterList.add(item.name)
        }
        mBinding.spinnerCommunitylistFilter1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = mViewModel.mCommunityInfo.communityCategorySub.categoryFilterList[position]
                mBinding.textviewCommunitylistFilter1.text = selectedCategory.name
                mViewModel.mPage = 0
                mViewModel.mFilterId = selectedCategory.id.toLong()
                mViewModel.getCommunityList()
            }
        }

        mBinding.spinnerCommunitylistFilter2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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

    enum class CommunityListType(val type: String) {
        TEXT("TEXT"), IMAGE("IMAGE")
    }

    companion object {
        @JvmStatic
        @BindingAdapter("communitySpinner")
        fun Spinner.bindCommunitySpinner(list: MutableList<String>) {
            if (list.isNotEmpty()) {
                if (this.adapter == null) {
                    this.adapter = CommunitySpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_community_spinner, list)
                } else {
                    (this.adapter as CommunitySpinnerAdapter).setItems(list)
                }
                this.setSelection(0)
            }
        }

//        @JvmStatic
//        @BindingAdapter("android:textColor")
//        fun TextView.bindColor(resId: Int?) {
//            if (resId != null)
//                this.setTextColor(resources.getColor(resId))
//            else
//                this.setTextColor(resources.getColor(R.color.warm_grey))
//        }
    }

}