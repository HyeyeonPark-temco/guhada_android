package io.temco.guhada.view.activity

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.OrderChangeCause
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.CancelOrderViewModel
import io.temco.guhada.databinding.ActivityRequestcancelorderBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.OrderChangeCauseAdapter
import io.temco.guhada.view.adapter.payment.PaymentSpinnerAdapter

/**
 * 주문 취소 신청 Activity
 * @author Hyeyeon Park
 * @since 2019.08.07
 */
class CancelOrderActivity : BindActivity<ActivityRequestcancelorderBinding>() {
    private lateinit var mViewModel: CancelOrderViewModel

    override fun getBaseTag(): String = CancelOrderActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_requestcancelorder

    override fun getViewType(): Type.View = Type.View.CANCEL_ORDER

    override fun init() {
        initViewModel()
        initHeader()
        initOrderInfo()
        initProductInfo()
        initCauseInfo()
        initButton()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = CancelOrderViewModel()
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null && ::mViewModel.isInitialized) mViewModel.purchaseOrder = it as PurchaseOrder
        }
        mViewModel.successCancelOrderTask = {
            val intent = Intent(this, SuccessCancelOrderActivity::class.java)
            intent.putExtra("purchaseOrder", mViewModel.purchaseOrder)
            startActivity(intent)
            finish()
        }
    }

    private fun initHeader() {
        mBinding.includeRequestcancelorderHeader.title = resources.getString(R.string.requestorderstatus_cancel_title)
        mBinding.includeRequestcancelorderHeader.setOnClickBackButton { finish() }
    }

    private fun initOrderInfo() {
        mBinding.includeRequestcancelorderOrderinfo.orderNumber = mViewModel.purchaseOrder.purchaseId.toInt()
        mBinding.includeRequestcancelorderOrderinfo.orderTimeStamp = mViewModel.purchaseOrder.orderTimestamp
    }

    private fun initProductInfo() {
        mBinding.includeRequestcancelorderProductinfo.imageUrl = mViewModel.purchaseOrder.imageUrl
        mBinding.includeRequestcancelorderProductinfo.brandName = mViewModel.purchaseOrder.brandName
        mBinding.includeRequestcancelorderProductinfo.productName = "${mViewModel.purchaseOrder.season} ${mViewModel.purchaseOrder.dealName}"
        mBinding.includeRequestcancelorderProductinfo.optionStr = mViewModel.purchaseOrder.getOptionStr()
        mBinding.includeRequestcancelorderProductinfo.price = mViewModel.purchaseOrder.discountPrice
        mBinding.includeRequestcancelorderProductinfo.purchaseStatusText = mViewModel.purchaseOrder.purchaseStatusText
    }

    private fun initCauseInfo() {
        mBinding.includeRequestcancelorderCause.causeList = mViewModel.causes
        mBinding.includeRequestcancelorderCause.quantityTitle = getString(R.string.requestorderstatus_cancel_quantity)
        mBinding.includeRequestcancelorderCause.quantity = mViewModel.quantity
        mBinding.includeRequestcancelorderCause.defaultMessage = getString(R.string.requestorderstatus_cancel_cause)
        mBinding.includeRequestcancelorderCause.hintMessage = getString(R.string.requestorderstatus_cancel_hint_cause)
        mBinding.includeRequestcancelorderCause.sellerName = mViewModel.purchaseOrder.sellerName
        mBinding.includeRequestcancelorderCause.setOnClickAmountPlus {
            if (mViewModel.quantity + 1 > mViewModel.purchaseOrder.quantity)
                ToastUtil.showMessage(getString(R.string.requestorderstatus_common_message_overmaxquantity))
            else mViewModel.quantity += 1
            mBinding.includeRequestcancelorderCause.quantity = mViewModel.quantity
            mBinding.executePendingBindings()
        }
        mBinding.includeRequestcancelorderCause.setOnClickAmountMinus {
            if (mViewModel.quantity - 1 > 0)
                mViewModel.quantity -= 1
            else
                ToastUtil.showMessage(getString(R.string.requestorderstatus_common_message_overminquantity))
            mBinding.includeRequestcancelorderCause.quantity = mViewModel.quantity
            mBinding.executePendingBindings()
        }
        mBinding.includeRequestcancelorderCause.spinnerRequestorderstatusCause.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mViewModel.selectedCausePos = position
                mBinding.includeRequestcancelorderCause.defaultMessage = mViewModel.causes[position].label
                mBinding.executePendingBindings()
            }
        }
    }

    private fun initButton() {
        mBinding.includeRequestcancelorderButton.cancelText = getString(R.string.requestorderstatus_cancel_button_submit)
        mBinding.includeRequestcancelorderButton.confirmText = getString(R.string.requestorderstatus_cancel_button_submit)
        mBinding.includeRequestcancelorderButton.setOnClickCancel { finish() }
        mBinding.includeRequestcancelorderButton.setOnClickConfirm {
            mViewModel.cause = mBinding.includeRequestcancelorderCause.edittextRequestorderstatusCause.text.toString()
            mViewModel.cancelOrder()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("cause")
        fun Spinner.bindCause(list: MutableList<OrderChangeCause>) {
            if (list.isNotEmpty()) {
                if (list[list.size - 1].label != resources.getString(R.string.payment_hint_shippingmemo))
                    list.add(OrderChangeCause().apply { this.label = resources.getString(R.string.requestorderstatus_cancel_cause) })

                if (this.adapter == null) {
                    this.adapter = OrderChangeCauseAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_cancelorder_spinner, list)
                } else {
                    (this.adapter as OrderChangeCauseAdapter).setItems(list)
                }
                this.setSelection(list.size - 1)
            }
        }

    }

}