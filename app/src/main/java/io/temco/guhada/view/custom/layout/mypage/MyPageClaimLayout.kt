package io.temco.guhada.view.custom.layout.mypage

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimViewModel
import io.temco.guhada.databinding.CustomlayoutMypageClaimBinding
import io.temco.guhada.view.WrapContentLinearLayoutManager
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

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_claim
    override fun init() {
        mViewModel = MyPageClaimViewModel(context)
        mBinding.viewModel = mViewModel

        mBinding.recyclerviewMypageclaimlayoutList.layoutManager = WrapContentLinearLayoutManager(context as Activity, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewMypageclaimlayoutList.setHasFixedSize(true)

        (mBinding.recyclerviewMypageclaimlayoutList.layoutManager as WrapContentLinearLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerviewMypageclaimlayoutList.layoutManager as WrapContentLinearLayoutManager).recycleChildrenOnDetach = true

        mBinding.swipeRefreshLayout.setOnRefreshListener(this)

        EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode.requestCode) {
               RequestCode.MODIFY_CLAIM.flag -> {
                   mViewModel.getListAdapter().items[mViewModel.selectedIndex].inquiry.inquiry = (requestCode.data as Claim).inquiry
                   mViewModel.getListAdapter().notifyItemChanged(mViewModel.selectedIndex)
               }
            }
        }
    }

    override fun onRefresh() {
        if (CustomLog.flag) CustomLog.L("MyPageClaimLayout", "onRefresh ", "init -----")
        mViewModel.reloadRecyclerView(object : OnSwipeRefreshResultListener {
            override fun onResultCallback() {
                if (CustomLog.flag) CustomLog.L("MyPageClaimLayout", "onResultCallback ", "init -----")
                this@MyPageClaimLayout.handler.postDelayed({
                    mViewModel.getListAdapter().notifyDataSetChanged()
                    if (CustomLog.flag) CustomLog.L("MyPageClaimLayout", "onResultCallback ", "getListAdapter -----", mViewModel.getListAdapter().items.size)
                    mBinding.swipeRefreshLayout.isRefreshing = false
                },200)
            }
        })
    }

    override fun onFocusView() {

    }

    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {

    }
}