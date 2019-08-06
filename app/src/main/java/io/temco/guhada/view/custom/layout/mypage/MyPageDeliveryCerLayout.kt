package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.mypage.MyPageDeliveryCerViewModel
import io.temco.guhada.databinding.CustomlayoutMypageDeliveryCerBinding
import io.temco.guhada.view.adapter.mypage.MyPageDeliveryAdapter
import io.temco.guhada.view.adapter.mypage.MyPageDeliveryCerAdapter
import io.temco.guhada.view.custom.CustomCalendarFilter
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 취소교환환불 화면
 * @author Hyeyeon Park
 * @since 2019.08.06
 *
 */
class MyPageDeliveryCerLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageDeliveryCerBinding, MyPageDeliveryCerViewModel>(context, attrs, defStyleAttr), CustomCalendarFilter.CustomCalendarListener, SwipeRefreshLayout.OnRefreshListener {
    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_delivery_cer
    override fun init() {
        mBinding.lifecycleOwner = this
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mBinding.recyclerviewMypagedeliverycer.adapter = MyPageDeliveryCerAdapter()

        mViewModel = MyPageDeliveryCerViewModel(context)
        mViewModel.cancelOrderHistory.observe(this, Observer {
            if (mBinding.recyclerviewMypagedeliverycer.adapter != null)
                (mBinding.recyclerviewMypagedeliverycer.adapter as MyPageDeliveryCerAdapter).setItems(it.orderItemList)
            else
                mBinding.recyclerviewMypagedeliverycer.adapter = MyPageDeliveryCerAdapter().apply { this.list = it.orderItemList }
            mBinding.executePendingBindings()
        })

        initCalendarFilter()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    override fun onRefresh() {
        mViewModel.page = 1
        mViewModel.getCancelOrderStatus()
        mViewModel.getCancelOrderHistories()
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    private fun initCalendarFilter() {
        mBinding.calendarfilterMypageDeliver.mListener = this
        mBinding.calendarfilterMypageDeliver.setPeriod(0)
        mBinding.calendarfilterMypageDeliver.setDate(7)
        mViewModel.getCancelOrderStatus()
        mViewModel.getCancelOrderHistories()
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

    // CustomCalendarListener
    override fun onClickWeek() = changeDate(startDate = mBinding.calendarfilterMypageDeliver.startDate, endDate = mBinding.calendarfilterMypageDeliver.endDate)
    override fun onClickMonth() = changeDate(startDate = mBinding.calendarfilterMypageDeliver.startDate, endDate = mBinding.calendarfilterMypageDeliver.endDate)
    override fun onClickThreeMonth() = changeDate(startDate = mBinding.calendarfilterMypageDeliver.startDate, endDate = mBinding.calendarfilterMypageDeliver.endDate)
    override fun onClickYear() = changeDate(startDate = mBinding.calendarfilterMypageDeliver.startDate, endDate = mBinding.calendarfilterMypageDeliver.endDate)
    override fun onClickCheck(startDate: String, endDate: String) = changeDate(startDate = startDate, endDate = endDate)

    override fun onChangeDate(startDate: String, endDate: String) {
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            mViewModel.page = 1
            mViewModel.startDate = CommonUtil.convertDateToTimeStamp(startDate, ".")
            mViewModel.endDate = CommonUtil.convertDateToTimeStamp(endDate, ".")
        }
    }

    private fun changeDate(startDate: String, endDate: String) {
        mViewModel.page = 1
        mViewModel.startDate = CommonUtil.convertDateToTimeStamp(startDate, ".")
        mViewModel.endDate = CommonUtil.convertDateToTimeStamp(endDate, ".")
        mViewModel.getCancelOrderStatus()
        mViewModel.getCancelOrderHistories()
    }
}