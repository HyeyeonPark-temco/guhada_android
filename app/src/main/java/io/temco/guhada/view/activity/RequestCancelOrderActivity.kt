package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
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
        mViewModel.purchaseOrder.observe(this, Observer {
            if (it.purchaseId > 0) {
                initOrderInfo(it)
                initProductInfo(it)
                initCauseInfo(it)
                initButton()

                mViewModel.mOrderProdGroupId = it.orderProdGroupId
                mViewModel.getExpectedRefundPriceForRequest(it.quantity)
                mBinding.viewModel = mViewModel
                mBinding.executePendingBindings()
            }
        })

        mViewModel.successCancelOrderTask = {
            val intent = Intent(this, SuccessCancelOrderActivity::class.java)
            intent.putExtra("purchaseOrder", it)
            startActivity(intent)
            setResult(Activity.RESULT_OK)
            finish()
        }
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
        mBinding.includeRequestcancelorderProductinfo.price = purchaseOrder.orderPrice
        mBinding.includeRequestcancelorderProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText
    }

    private fun initCauseInfo(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestcancelorderCause.causeList = purchaseOrder.cancelReasonList
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
                mViewModel.getExpectedRefundPriceForRequest(quantity + 1)
            }
        }
        mBinding.includeRequestcancelorderCause.setOnClickAmountMinus {
            val quantity = mBinding.includeRequestcancelorderCause.quantity ?: 0
            if (quantity - 1 <= 0) ToastUtil.showMessage(getString(R.string.requestorderstatus_common_message_overminquantity))
            else {
                mBinding.includeRequestcancelorderCause.quantity = quantity - 1
                mViewModel.getExpectedRefundPriceForRequest(quantity - 1)
            }
        }
        mBinding.includeRequestcancelorderCause.spinnerRequestorderstatusCause.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mViewModel.selectedCausePos = position
                mBinding.includeRequestcancelorderCause.defaultMessage = mViewModel.purchaseOrder.value?.cancelReasonList?.get(position)?.label
                mBinding.executePendingBindings()
            }
        }
    }

    private fun initButton() {
        mBinding.includeRequestcancelorderButton.cancelText = getString(R.string.common_cancel)
        mBinding.includeRequestcancelorderButton.confirmText = getString(R.string.requestorderstatus_cancel_button_submit)
        mBinding.includeRequestcancelorderButton.setOnClickCancel { finish() }
        mBinding.includeRequestcancelorderButton.setOnClickConfirm {
            mViewModel.cause = mBinding.includeRequestcancelorderCause.edittextRequestorderstatusCause.text.toString()
            mViewModel.cancelOrder()
        }
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