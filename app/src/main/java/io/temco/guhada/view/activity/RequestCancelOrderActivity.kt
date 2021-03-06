package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.PurchaseStatus
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.OrderChangeCause
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.mypage.delivery.CancelOrderViewModel
import io.temco.guhada.databinding.ActivityRequestcancelorderBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.OrderChangeCauseAdapter

/**
 * 주문 취소 신청 Activity
 * @author Hyeyeon Park
 * @since 2019.08.07
 */
class RequestCancelOrderActivity : BindActivity<ActivityRequestcancelorderBinding>() {
    private lateinit var mViewModel: CancelOrderViewModel

    override fun getBaseTag(): String = RequestCancelOrderActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_requestcancelorder

    override fun getViewType(): Type.View = Type.View.REQUEST_CANCEL_ORDER

    lateinit var loadingIndicatorUtil: LoadingIndicatorUtil

    override fun init() {
        initViewModel()
        initHeader()
        initExpectedRefundPrice()
    }



    private fun initViewModel() {
        mViewModel = CancelOrderViewModel()

        intent.getLongExtra("orderProdGroupId", 0).let {
            if (it > 0 && ::mViewModel.isInitialized) {
                mViewModel.mOrderProdGroupId = it
                mViewModel.getClaimForm(it)
            }
        }

        mViewModel.mPurchaseOrder.observe(this, Observer {
            if (it.purchaseId > 0) {
                initOrderInfo(it)
                initProductInfo(it)
                initCauseInfo(it)
                initButton()

                mViewModel.mQuantity = it.quantity
                mBinding.includeRequestcancelorderRefund.constraintlayoutRequestcancelorderRefund.visibility = if (it.orderStatus == PurchaseStatus.WAITING_PAYMENT.status) View.GONE else View.VISIBLE
                mViewModel.mOrderProdGroupId = it.orderProdGroupId
                mViewModel.getExpectedRefundPriceForRequest(it.quantity)
                mBinding.viewModel = mViewModel
                mBinding.executePendingBindings()
            }
        })

        mViewModel.mShowIndicatorTask = { showIndicator() }
        mViewModel.mFailCancelOrderTask = { hideIndicator() }

        mViewModel.mSuccessCancelOrderTask = {
            loadingIndicatorUtil.dismiss()
            val intent = Intent(this, SuccessCancelOrderActivity::class.java)
            intent.putExtra("purchaseOrder", it)
            startActivity(intent)
            setResult(Activity.RESULT_OK)
            finish()
        }

        mViewModel.mCloseActivityTask = {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun showIndicator() {
        if (!::loadingIndicatorUtil.isInitialized)
            loadingIndicatorUtil = LoadingIndicatorUtil(this@RequestCancelOrderActivity)
        loadingIndicatorUtil.show()
    }

    private fun hideIndicator() {
        if (::loadingIndicatorUtil.isInitialized)
            loadingIndicatorUtil.dismiss()
    }

    private fun initExpectedRefundPrice() {
        mViewModel.mExpectedRefundPrice.observe(this, Observer {
            mBinding.includeRequestcancelorderRefund.expectedRefundPrice = it
            mBinding.executePendingBindings()
        })
    }

    private fun initHeader() {
        mBinding.includeRequestcancelorderHeader.title = resources.getString(R.string.requestorderstatus_cancel_title)
        mBinding.includeRequestcancelorderHeader.setOnClickBackButton { finish() }
    }

    private fun initOrderInfo(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestcancelorderOrderinfo.orderNumber = purchaseOrder.purchaseId.toInt()
        mBinding.includeRequestcancelorderOrderinfo.orderTimeStamp = purchaseOrder.orderTimestamp
    }

    private fun initProductInfo(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestcancelorderProductinfo.imageUrl = purchaseOrder.imageUrl
        mBinding.includeRequestcancelorderProductinfo.brandName = purchaseOrder.brandName
        mBinding.includeRequestcancelorderProductinfo.productName = if (purchaseOrder.season.isNullOrEmpty()) purchaseOrder.productName else "${purchaseOrder.season} ${purchaseOrder.productName}"
        mBinding.includeRequestcancelorderProductinfo.optionStr = purchaseOrder.getOptionStr()
        mBinding.includeRequestcancelorderProductinfo.price = purchaseOrder.originalPrice
        mBinding.includeRequestcancelorderProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText
    }

    private fun initCauseInfo(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestcancelorderCause.quantityTitle = getString(R.string.requestorderstatus_cancel_quantity)
        mBinding.includeRequestcancelorderCause.quantity = purchaseOrder.quantity
        mBinding.includeRequestcancelorderCause.defaultMessage = getString(R.string.requestorderstatus_cancel_cause)
        mBinding.includeRequestcancelorderCause.hintMessage = getString(R.string.requestorderstatus_cancel_hint_cause)
        mBinding.includeRequestcancelorderCause.sellerName = purchaseOrder.sellerName
        mBinding.includeRequestcancelorderCause.setOnClickAmountPlus {
            val quantity = mBinding.includeRequestcancelorderCause.quantity ?: 0
            if (quantity + 1 > purchaseOrder.quantity) ToastUtil.showMessage(getString(R.string.requestorderstatus_common_message_overmaxquantity))
            else {
                mBinding.includeRequestcancelorderCause.quantity = quantity + 1
                mViewModel.mQuantity = quantity + 1
                mViewModel.getExpectedRefundPriceForRequest(quantity + 1)
            }
        }
        mBinding.includeRequestcancelorderCause.setOnClickAmountMinus {
            val quantity = mBinding.includeRequestcancelorderCause.quantity ?: 0
            if (quantity - 1 <= 0) ToastUtil.showMessage(getString(R.string.requestorderstatus_common_message_overminquantity))
            else {
                mBinding.includeRequestcancelorderCause.quantity = quantity - 1
                mViewModel.mQuantity = quantity - 1
                mViewModel.getExpectedRefundPriceForRequest(quantity - 1)
            }
        }

        /**
         * Custom Spinner View 변경
         * @since 2019.12.13
         */
        val list = mutableListOf<Any>()
        Observable.fromIterable(purchaseOrder.cancelReasonList).subscribe {
            list.add(it.label)
        }.dispose()
        mBinding.includeRequestcancelorderCause.spinnerRequestorderstatusCause1.setPlaceHolder(getString(R.string.requestorderstatus_cancel_cause))
        mBinding.includeRequestcancelorderCause.spinnerRequestorderstatusCause1.setItems(list)
        mBinding.includeRequestcancelorderCause.spinnerRequestorderstatusCause1.setOnItemClickTask { position ->
            mViewModel.mSelectedCausePos = position
        }
    }

    private fun initButton() {
        mBinding.includeRequestcancelorderButton.cancelText = getString(R.string.common_cancel)
        mBinding.includeRequestcancelorderButton.confirmText = getString(R.string.requestorderstatus_cancel_button_submit)
        mBinding.includeRequestcancelorderButton.setOnClickCancel { finish() }
        mBinding.includeRequestcancelorderButton.setOnClickConfirm {
            mViewModel.mCause = mBinding.includeRequestcancelorderCause.edittextRequestorderstatusCause.text.toString()
            mViewModel.cancelOrder()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::loadingIndicatorUtil.isInitialized) loadingIndicatorUtil.dismiss()
    }

    companion object {

        /**
         * requestType: 0 취소, 1 교환, 2 반품
         */
        @JvmStatic
        @BindingAdapter(value = ["cause", "requestType"])
        fun Spinner.bindCause(list: MutableList<OrderChangeCause>?, requestType: Int = 0) {
            if (list?.isNotEmpty() == true) {
                if (list[list.size - 1].label != resources.getString(R.string.payment_hint_shippingmemo))
                    list.add(OrderChangeCause().apply {
                        this.label = when (requestType) {
                            0 -> resources.getString(R.string.requestorderstatus_cancel_cause)
                            1 -> resources.getString(R.string.requestorderstatus_exchange_cause)
                            2 -> resources.getString(R.string.requestorderstatus_refund_cause)
                            else -> resources.getString(R.string.requestorderstatus_cancel_cause)
                        }
                    })

                this.adapter = OrderChangeCauseAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_cancelorder_spinner, list)
                this.setSelection(list.size - 1)
            }
        }

    }

}