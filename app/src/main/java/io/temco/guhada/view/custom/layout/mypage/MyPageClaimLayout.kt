package io.temco.guhada.view.custom.layout.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimViewModel
import io.temco.guhada.databinding.CustomlayoutMypageClaimBinding
import io.temco.guhada.view.WrapContentLinearLayoutManager
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 상품문의 화면
 *
 */
class MyPageClaimLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageClaimBinding, MyPageClaimViewModel>(context, attrs, defStyleAttr) , SwipeRefreshLayout.OnRefreshListener{

    private lateinit var recyclerLayoutManager1 : WrapContentLinearLayoutManager
    private lateinit var recyclerLayoutManager2 : WrapContentLinearLayoutManager

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_claim
    override fun init() {
        mViewModel = MyPageClaimViewModel(context)
        mBinding.viewModel = mViewModel

        recyclerLayoutManager1 = WrapContentLinearLayoutManager(context as Activity, LinearLayoutManager.VERTICAL, false).apply {
            orientation = RecyclerView.VERTICAL
            recycleChildrenOnDetach = true
        }
        mBinding.recyclerviewMypageclaimlayoutList1.layoutManager = recyclerLayoutManager1
        mBinding.recyclerviewMypageclaimlayoutList1.setHasFixedSize(true)
        mBinding.recyclerviewMypageclaimlayoutList1.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (mViewModel.getListAdapter1().itemCount - recyclerLayoutManager1.findLastVisibleItemPosition() <= 2 && !mViewModel.isLoading1 && mViewModel.totalPageNum1 > mViewModel.pageNum1+1) {
                    mViewModel.isLoading1 = true
                    mViewModel.getMoreCalimList(mViewModel.pageNum1+1)
                }
            }
        })

        recyclerLayoutManager2 = WrapContentLinearLayoutManager(context as Activity, LinearLayoutManager.VERTICAL, false).apply {
            orientation = RecyclerView.VERTICAL
            recycleChildrenOnDetach = true
        }
        mBinding.recyclerviewMypageclaimlayoutList2.layoutManager = recyclerLayoutManager2
        mBinding.recyclerviewMypageclaimlayoutList2.setHasFixedSize(true)
        mBinding.recyclerviewMypageclaimlayoutList2.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (mViewModel.getListAdapter2().itemCount - recyclerLayoutManager2.findLastVisibleItemPosition() <= 2 && !mViewModel.isLoading2 && mViewModel.totalPageNum2 > mViewModel.pageNum2+1) {
                    mViewModel.isLoading2 = true
                    mViewModel.getMoreSellerCalimList(mViewModel.pageNum2+1)
                }
            }
        })


        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mBinding.setClickListenerClaimStatus {
            val popup = PopupMenu(this@MyPageClaimLayout.context, it)
            popup.menuInflater.inflate(R.menu.menu_mypage_claim, popup.menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.maypage_claim_status1 -> mViewModel.onShippingMemoSelected(0)
                        R.id.maypage_claim_status2 -> mViewModel.onShippingMemoSelected(1)
                        R.id.maypage_claim_status3 -> mViewModel.onShippingMemoSelected(2)
                    }
                    return true
                }
            })
            popup.show()
        }

        setEventBus()
        mBinding.executePendingBindings()
    }

    override fun onRefresh() {
        if (CustomLog.flag) CustomLog.L("MyPageClaimLayout", "onRefresh ", "init -----")
        mViewModel.reloadRecyclerView(object : OnSwipeRefreshResultListener {
            override fun onResultCallback() {
                if (CustomLog.flag) CustomLog.L("MyPageClaimLayout", "onResultCallback ", "init -----")
                this@MyPageClaimLayout.handler.postDelayed({
                    if(mViewModel.mypageClaimTabVisibleSwitch.get() == 0){
                        mViewModel.getListAdapter1().notifyDataSetChanged()
                        if (CustomLog.flag) CustomLog.L("MyPageClaimLayout", "onResultCallback ", "getListAdapter1 -----", mViewModel.getListAdapter1().items.size)
                    }else if(mViewModel.mypageClaimTabVisibleSwitch.get() == 0){
                        mViewModel.getListAdapter2().notifyDataSetChanged()
                        if (CustomLog.flag) CustomLog.L("MyPageClaimLayout", "onResultCallback ", "getListAdapter2 -----", mViewModel.getListAdapter2().items.size)
                    }
                    mBinding.swipeRefreshLayout.isRefreshing = false
                },200)
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun setEventBus(){
         EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode.requestCode) {
                RequestCode.MODIFY_CLAIM.flag -> {
                    mViewModel.getListAdapter1().items[mViewModel.selectedIndex].inquiry.inquiry = (requestCode.data as Claim).inquiry
                    mViewModel.getListAdapter1().notifyItemChanged(mViewModel.selectedIndex)
                }
            }
        }
    }


    override fun onFocusView() { }
    override fun onReleaseView() { }
    override fun onStart() { }
    override fun onResume() { }
    override fun onPause() { }
    override fun onStop() { }
    override fun onDestroy() { }
}