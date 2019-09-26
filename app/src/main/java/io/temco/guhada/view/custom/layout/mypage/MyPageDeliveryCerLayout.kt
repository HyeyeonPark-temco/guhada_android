package io.temco.guhada.view.custom.layout.mypage

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.data.viewmodel.mypage.MyPageDeliveryCerViewModel
import io.temco.guhada.databinding.CustomlayoutMypageDeliveryCerBinding
import io.temco.guhada.view.adapter.mypage.MyPageDeliveryAdapter
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
        mBinding.recyclerviewMypagedeliverycer.adapter = MyPageDeliveryAdapter().apply {
            this.type = MyPageDeliveryAdapter.Type.DeliveryCer.type
            this.editShippingAddressTask = { purchaseId -> mViewModel.editShippingAddress(purchaseId) }
        }

        mViewModel = MyPageDeliveryCerViewModel(context)
        mViewModel.cancelOrderHistory.observe(this, Observer {
            //            if (mBinding.recyclerviewMypagedeliverycer.adapter != null)
//                (mBinding.recyclerviewMypagedeliverycer.adapter as MyPageDeliveryAdapter).setItems(mViewModel.cancelOrderHistory.value?.orderItemList
//                        ?: it.orderItemList)
//            else
            mBinding.recyclerviewMypagedeliverycer.adapter = MyPageDeliveryAdapter().apply { this.list = it.orderItemList }

            if (it.totalPage == 1 && it.orderItemList.isEmpty()) {
                mBinding.imageviewMypagedeliverycerEmpty.visibility = View.VISIBLE
                mBinding.textviewMypagedeliverycerEmpty.visibility = View.VISIBLE
            } else {
                mBinding.imageviewMypagedeliverycerEmpty.visibility = View.GONE
                mBinding.textviewMypagedeliverycerEmpty.visibility = View.GONE
            }

            mBinding.executePendingBindings()
        })

        initCalendarFilter()
        setEventBus()
        mViewModel.setDate(7) // [default] before 1 week

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    @SuppressLint("CheckResult")
    private fun setEventBus() {
        EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode.requestCode) {
                RequestCode.WITHDRAW.flag,
                RequestCode.CONFIRM_PURCHASE.flag -> {
                    mViewModel.page = 1
                    mViewModel.getCancelOrderStatus()
                    mViewModel.getCancelOrderHistories()
                }
            }
        }
    }

    override fun onRefresh() {
        mViewModel.page = 1
        mViewModel.getCancelOrderStatus()
        mViewModel.getCancelOrderHistories()
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    private fun initCalendarFilter() {
        mBinding.calendarfilterMypageDeliver.mListener = this
        mBinding.calendarfilterMypageDeliver.setPeriod(CustomCalendarFilter.CalendarPeriod.THREE_MONTH.pos)
        mBinding.calendarfilterMypageDeliver.setDate(CustomCalendarFilter.CalendarPeriod.THREE_MONTH.date)
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
    override fun onClickWeek() = changeDate()

    override fun onClickMonth() = changeDate()
    override fun onClickThreeMonth() = changeDate()
    override fun onClickYear() = changeDate()
    override fun onClickCheck(startDate: String, endDate: String) = changeDate()

    override fun onChangeDate(startDate: String, endDate: String) {
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            mViewModel.page = 1
            mViewModel.startDate = mBinding.calendarfilterMypageDeliver.startTimeStamp
            mViewModel.endDate = mBinding.calendarfilterMypageDeliver.endTimeStamp
        }
    }

    private fun changeDate() {
        mViewModel.page = 1
        mViewModel.startDate = mBinding.calendarfilterMypageDeliver.startTimeStamp
        mViewModel.endDate = mBinding.calendarfilterMypageDeliver.endTimeStamp
        mViewModel.getCancelOrderStatus()
        mViewModel.getCancelOrderHistories()
    }


}