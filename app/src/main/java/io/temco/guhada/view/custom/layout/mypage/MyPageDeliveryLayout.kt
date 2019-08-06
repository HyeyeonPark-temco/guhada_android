package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
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
        mViewModel = MyPageDeliveryViewModel(context)
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mBinding.calendarfilterMypageDeliver.mListener = this
        mViewModel.orderHistoryList.observe(this, androidx.lifecycle.Observer {
            val list = it.orderItemList
            mBinding.listContents.adapter = MyPageDeliveryAdapter(mViewModel).apply { this.list = list }
            mBinding.executePendingBindings()
        })
        mBinding.includeDeliveryProcess.viewModel = mViewModel

        mViewModel.getOrderStatus()
        mBinding.calendarfilterMypageDeliver.setPeriod(0)
        onClickWeek() // [default] before 1 week

        mRequestManager = Glide.with(this)
        setLinkText()
    }

    // CustomCalendarListener
    private fun calendarCallback(startDate: String, endDate: String) {
        mBinding.calendarfilterMypageDeliver.startDate = startDate
        mBinding.calendarfilterMypageDeliver.endDate = endDate
        mViewModel.getOrders()
    }

    override fun onClickWeek() = mViewModel.setDate(7) { startDate, endDate -> calendarCallback(startDate, endDate) }
    override fun onClickMonth() = mViewModel.setDate(30) { startDate, endDate -> calendarCallback(startDate, endDate) }
    override fun onClickThreeMonth() = mViewModel.setDate(90) { startDate, endDate -> calendarCallback(startDate, endDate) }
    override fun onClickYear() = mViewModel.setDate(365) { startDate, endDate -> calendarCallback(startDate, endDate) }
    override fun onChangeDate(startDate: String, fromDate: String) {

    }

    override fun onClickCheck(startDate: String, endDate: String) {
        mViewModel.startDate = startDate
        mViewModel.endDate = endDate
        mViewModel.getOrders()
    }

    private fun setLinkText() {
        // Sales Number
        mBinding.layoutInformation.textInformationSalesNumber.setMovementMethod(LinkMovementMethod.getInstance())
        mBinding.layoutInformation.textInformationSalesNumber.setText(TextUtil.createTextWithLink(context!!,
                R.string.information_company_sales_number, R.string.information_confirm_company,
                R.dimen.text_11, true
        ) {
            if (true) {
                CommonUtil.debug("Company Info!")
            }
        }, TextView.BufferType.SPANNABLE)
        // Description
        mBinding.layoutInformation.textInformationDescription.setMovementMethod(LinkMovementMethod.getInstance())
        mBinding.layoutInformation.textInformationDescription.setText(TextUtil.createTextWithLink(context!!,
                R.string.information_description, R.string.information_confirm_service,
                R.dimen.text_11, true
        ) {
            if (true) {
                CommonUtil.debug("Sevice Confirm!")
            }
        }, TextView.BufferType.SPANNABLE)
    }


    private fun showRewardDialog(message: String) {
        val md = MessageDialog()
        md.setMessage(message)
        md.show((context as MainActivity).supportFragmentManager, "OrderShipFragment")
    }


    override fun onRefresh() {
        mBinding.swipeRefreshLayout.isRefreshing = false
        mViewModel.getOrders()
        mViewModel.getOrderStatus()
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