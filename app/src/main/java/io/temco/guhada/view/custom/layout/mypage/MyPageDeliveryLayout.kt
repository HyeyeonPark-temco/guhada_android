package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.TextUtil
import io.temco.guhada.data.viewmodel.mypage.MyPageDeliveryViewModel
import io.temco.guhada.databinding.CustomlayoutMypageDeliveryBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.mypage.MyPageDeliveryAdapter
import io.temco.guhada.view.custom.CustomCalendarFilter
import io.temco.guhada.view.custom.dialog.MessageDialog
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * created 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 주문배송 화면
 * @author Hyeyeon Park
 * @since 2019.07.26
 *
 */
class MyPageDeliveryLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageDeliveryBinding, MyPageDeliveryViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener, CustomCalendarFilter.CustomCalendarListener {
    // -------- LOCAL VALUE --------
    private var mRequestManager: RequestManager? = null

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_delivery
    override fun init() {
        mBinding.lifecycleOwner = this
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)

        mViewModel = MyPageDeliveryViewModel(context)
        mViewModel.orderHistoryList.observe(this, androidx.lifecycle.Observer {
            mBinding.listContents.adapter = MyPageDeliveryAdapter().apply {
                this.list = it.orderItemList
                this.editShippingAddressTask = { purchaseId -> mViewModel.editShippingAddress(purchaseId) }
            }

            if (it.totalPage == 1 && it.orderItemList.isEmpty()) {
                mBinding.imageviewMypageDeliveryEmpty.visibility = View.VISIBLE
                mBinding.textviewMypageDeliveryEmpty.visibility = View.VISIBLE
            } else {
                mBinding.imageviewMypageDeliveryEmpty.visibility = View.GONE
                mBinding.textviewMypageDeliveryEmpty.visibility = View.GONE
            }

            mBinding.linearlayoutMypagedeliverycerMore.visibility = if (it.totalPage != it.page) View.VISIBLE else View.GONE

            mBinding.executePendingBindings()
        })

        mBinding.viewModel = mViewModel
        mBinding.includeDeliveryProcess.viewModel = mViewModel

        mViewModel.getOrderStatus()
        initCalendarFilter()

        mRequestManager = Glide.with(this)
    }

    // CustomCalendarListener

    override fun onClickWeek() = changeDate()
    override fun onClickMonth() = changeDate()
    override fun onClickThreeMonth() = changeDate()
    override fun onClickYear() = changeDate()
    override fun onChangeDate(startDate: String, endDate: String) {
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            mViewModel.page = 1
            mViewModel.startDate = mBinding.calendarfilterMypageDeliver.startTimeStamp
            mViewModel.endDate = mBinding.calendarfilterMypageDeliver.endTimeStamp
        }
    }

    private fun changeDate() {
        mViewModel.page = 1
        mViewModel.startDate = mBinding.calendarfilterMypageDeliver.startTimeStamp  //  CommonUtil.convertDateToTimeStamp(startDate, ".")
        mViewModel.endDate = mBinding.calendarfilterMypageDeliver.endTimeStamp // CommonUtil.convertDateToTimeStamp(endDate, ".")
        mViewModel.getOrderStatus()
        mViewModel.getOrders()
    }

    override fun onClickCheck(startDate: String, endDate: String) = changeDate()

    override fun onRefresh() {
        mViewModel.page = 1
        mViewModel.getOrders()
        mViewModel.getOrderStatus()
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    private fun initCalendarFilter() {
        mBinding.calendarfilterMypageDeliver.mListener = this
        mBinding.calendarfilterMypageDeliver.setPeriod(0)
        mBinding.calendarfilterMypageDeliver.setDate(7)
    }


////////////////////////////////////////////////

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