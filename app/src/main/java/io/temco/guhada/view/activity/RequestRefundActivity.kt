package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RefundCause
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.mypage.delivery.RequestRefundViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ShippingCompanySpinnerAdapter

/**
 * 반품 신청
 * @author Hyeyeon Park
 * @since 2019.08.20
 */
class RequestRefundActivity : BindActivity<io.temco.guhada.databinding.ActivityRequestrefundBinding>() {
    private lateinit var mViewModel: RequestRefundViewModel

    override fun getBaseTag(): String = RequestRefundActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_requestrefund
    override fun getViewType(): Type.View = Type.View.REQUEST_REFUND
    override fun init() {
        initViewModel()
        initHeader()
    }

    private fun initHeader() {
        mBinding.includeRequestrefundHeader.title = resources.getString(R.string.requestorderstatus_refund_title)
        mBinding.includeRequestrefundHeader.setOnClickBackButton { finish() }
    }

    // TODO 환불 계좌 정보
    private fun initBank(purchaseOrder: PurchaseOrder) {

    }

    private fun initViewModel() {
        mViewModel = RequestRefundViewModel()

        // 신청서 수정
        intent.getLongExtra("modifyOrderProdGroupId", 0).let {
            if (it > 0 && ::mViewModel.isInitialized) {
                mViewModel.mRefundRequest.orderProdGroupId = it
                mViewModel.mOrderProdGroupId = it
                mViewModel.getUpdateClaimForm(it)
            }
        }

        // 신규 반품 신청
        intent.getLongExtra("orderProdGroupId", 0).let {
            if (it > 0 && ::mViewModel.isInitialized) {
                mViewModel.mRefundRequest.orderProdGroupId = it
                mViewModel.mOrderProdGroupId = it
                mViewModel.getClaimForm(it)
            }
        }
        mViewModel.mPurchaseOrder.observe(this, Observer {
            if (it.purchaseId > 0) {
                initOrderInfo(it)
                initProductInfo(it)
                initCause(it)
                initSellerShipping()
                initCollection(it)
                initShippingPayment(it)
                initBank(it)
                initButton()
                mBinding.executePendingBindings()
            }
        })

        mViewModel.mSuccessRequestRefundTask = { purchaseOrder ->
            val intent = Intent(this@RequestRefundActivity, SuccessRequestRefundActivity::class.java)
            intent.putExtra("purchaseOrder", purchaseOrder)
            intent.putExtra("sellerAddress", mViewModel.mSellerAddress.value)
            intent.putExtra("refundRequest", mViewModel.mRefundRequest)

            startActivity(intent)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun initOrderInfo(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestrefundOrderinfo.orderNumber = purchaseOrder.purchaseId.toInt()
        mBinding.includeRequestrefundOrderinfo.orderTimeStamp = purchaseOrder.orderTimestamp
    }

    private fun initProductInfo(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestrefundProductinfo.imageUrl = purchaseOrder.imageUrl
        mBinding.includeRequestrefundProductinfo.brandName = purchaseOrder.brandName
        mBinding.includeRequestrefundProductinfo.productName = "${purchaseOrder.season} ${purchaseOrder.dealName}"
        mBinding.includeRequestrefundProductinfo.optionStr = purchaseOrder.getOptionStr()
        mBinding.includeRequestrefundProductinfo.price = purchaseOrder.discountPrice
        mBinding.includeRequestrefundProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText
    }

    private fun initCause(purchaseOrder: PurchaseOrder) {
        if (!purchaseOrder.returnReason.isNullOrEmpty()) {
            mBinding.includeRequestrefundCause.defaultMessage = getReason(purchaseOrder.returnReason)
            mBinding.includeRequestrefundCause.textviewRequestorderstatusCause.text = getReason(purchaseOrder.returnReason)
        }
        mBinding.includeRequestrefundCause.hintMessage = if (purchaseOrder.returnReasonDetail.isEmpty()) resources.getString(R.string.requestorderstatus_refund_hint_cause) else purchaseOrder.returnReasonDetail
        mBinding.includeRequestrefundCause.quantityTitle = resources.getString(R.string.requestorderstatus_refund_quantity)
        mBinding.includeRequestrefundCause.quantity = 1
        mBinding.includeRequestrefundCause.requestType = 2
        mBinding.includeRequestrefundCause.setOnClickAmountMinus {
            val quantity = mBinding.includeRequestrefundCause.quantity ?: 0
            if (quantity - 1 <= 0) ToastUtil.showMessage("반품 가능 최소 수량 1개")
            else mBinding.includeRequestrefundCause.quantity = quantity - 1

            mViewModel.mRefundRequest.quantity = mBinding.includeRequestrefundCause.quantity
                    ?: 0
        }
        mBinding.includeRequestrefundCause.setOnClickAmountPlus {
            val quantity = mBinding.includeRequestrefundCause.quantity ?: 0
            if (quantity + 1 > purchaseOrder.quantity) ToastUtil.showMessage("반품 가능 최대 수량 ${purchaseOrder.quantity}개")
            else mBinding.includeRequestrefundCause.quantity = quantity + 1

            mViewModel.mRefundRequest.quantity = mBinding.includeRequestrefundCause.quantity
                    ?: 0
        }
        mBinding.includeRequestrefundCause.causeList = purchaseOrder.returnReasonList
        mBinding.includeRequestrefundCause.spinnerRequestorderstatusCause.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (purchaseOrder.returnReasonList != null) {
                    if (purchaseOrder.returnReason.isNotEmpty() && position > (purchaseOrder.returnReasonList!!.size - 2)) {

                    } else {
                        val cause = mViewModel.mPurchaseOrder.value?.returnReasonList!![position]
                        mViewModel.mRefundRequest.refundReason = cause.code
                        mBinding.includeRequestrefundCause.defaultMessage = cause.label

                        mBinding.includeRequestrefundShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility =
                                if (cause.isFeeCharged) View.VISIBLE
                                else View.GONE

                        if (!cause.isFeeCharged)
                            mViewModel.mRefundRequest.claimShippingPriceType = ShippingPaymentType.NONE.type
                    }
                }
            }
        }
        mBinding.includeRequestrefundCause.edittextRequestorderstatusCause.addTextChangedListener {
            mViewModel.mRefundRequest.refundReasonDetail = it.toString()
        }
    }

    private fun initSellerShipping() {
        mBinding.includeRequestrefundSellershipping.title = resources.getString(R.string.requestorderstatus_refund_selleraddress)
        mViewModel.mSeller.observe(this, Observer {
            mBinding.includeRequestrefundSellershipping.sellerName = mViewModel.mSeller.value?.storeName
            mBinding.includeRequestrefundSellershipping.sellerPhone = mViewModel.mSeller.value?.claimTelephone
            mBinding.includeRequestrefundCause.sellerName = mViewModel.mSeller.value?.storeName
        })
        mViewModel.mSellerAddress.observe(this, Observer {
            mBinding.includeRequestrefundSellershipping.address = "[${it.zip}] ${it.roadAddress} ${it.detailAddress}"
        })

        mViewModel.getSellerInfo()
        mViewModel.getSellerDefaultReturnAddress()
    }

    private fun initCollection(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestrefundCollection.title = resources.getString(R.string.requestorderstatus_refund_way_title)
        mBinding.includeRequestrefundCollection.description = resources.getString(R.string.requestorderstatus_refund_way_description)

        // 신청서 수정 택배사
        if (!purchaseOrder.returnPickingShipCompany.isNullOrEmpty()) {
            mBinding.includeRequestrefundCollection.radiobuttonRequestorderstatusWayTrue.isChecked = true
            mBinding.includeRequestrefundCollection.textviewRequestorderstatusShippingcompany.text = purchaseOrder.returnPickingShipCompany

            mBinding.includeRequestrefundCollection.framelayoutRequestorderstatusShippingcompany.visibility = View.VISIBLE
            mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.visibility = View.VISIBLE

            if (!purchaseOrder.returnPickingInvoiceNo.isNullOrEmpty()) {
                mBinding.includeRequestrefundCollection.imageviewRequestorderstatusWarning.visibility = View.GONE
                mBinding.includeRequestrefundCollection.textviewRequestorderstatusWarning.visibility = View.GONE
                mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.setText(purchaseOrder.returnPickingInvoiceNo)
            }
        } else {
            mBinding.includeRequestrefundCollection.radiobuttonRequestorderstatusWayFalse.isChecked = true
        }

        // Listener
        mBinding.includeRequestrefundCollection.radiobuttonRequestorderstatusWayTrue.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mRefundRequest.alreadySend = true
                mBinding.includeRequestrefundCollection.radiobuttonRequestorderstatusWayFalse.isChecked = false

                // 택배사 및 송장번호 입력 란
                mBinding.includeRequestrefundCollection.framelayoutRequestorderstatusShippingcompany.visibility = View.VISIBLE
                mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.visibility = View.VISIBLE
                mBinding.includeRequestrefundCollection.textviewRequestorderstatusWarning.visibility = View.VISIBLE
                mBinding.includeRequestrefundCollection.imageviewRequestorderstatusWarning.visibility = View.VISIBLE
            }
        }
        mBinding.includeRequestrefundCollection.radiobuttonRequestorderstatusWayFalse.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mRefundRequest.alreadySend = false
                mBinding.includeRequestrefundCollection.radiobuttonRequestorderstatusWayTrue.isChecked = false

                // 택배사 및 송장번호 입력 란
                mBinding.includeRequestrefundCollection.framelayoutRequestorderstatusShippingcompany.visibility = View.GONE
                mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.visibility = View.GONE
                mBinding.includeRequestrefundCollection.textviewRequestorderstatusWarning.visibility = View.GONE
                mBinding.includeRequestrefundCollection.imageviewRequestorderstatusWarning.visibility = View.GONE
            }
        }

        mViewModel.mShippingCompanyList.observe(this, Observer {
            mBinding.includeRequestrefundCollection.spinnerRequestorderstatusShippingcompany.adapter = ShippingCompanySpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_payment_spinner, it)
            mBinding.includeRequestrefundCollection.spinnerRequestorderstatusShippingcompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val company = mViewModel.mShippingCompanyList.value?.get(position)
                    if (company != null) {
                        mViewModel.mRefundRequest.shippingCompanyCode = company.code
                        mViewModel.mRefundRequest.shippingCompanyName = company.name
                    }
                    mBinding.includeRequestrefundCollection.textviewRequestorderstatusShippingcompany.text = company?.name
                }
            }

            if (!purchaseOrder.returnPickingShipCompany.isNullOrEmpty()) { // 신청서 수정
                val code = purchaseOrder.returnPickingShipCompany
                for (i in 0 until it.size) {
                    if (code == it[i].code) {
                        mBinding.includeRequestrefundCollection.spinnerRequestorderstatusShippingcompany.setSelection(i)
                        break
                    }
                }
            } else {
                mBinding.includeRequestrefundCollection.spinnerRequestorderstatusShippingcompany.setSelection(it.size - 1)
            }
        })
        mViewModel.getShippingCompany()
    }

    private fun initShippingPayment(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestrefundShippingpayment.title = resources.getString(R.string.requestorderstatus_refund_shipping_title)
        mBinding.includeRequestrefundShippingpayment.textviewRequestorderstatusShippingpaymentDescription1.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_refund_shipping_description1))
        mBinding.includeRequestrefundShippingpayment.textviewRequestorderstatusShippingpaymentDescription2.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_refund_shipping_description2))
        mBinding.includeRequestrefundShippingpayment.isRefund = true

        if (purchaseOrder.returnShippingPriceType != null && purchaseOrder.returnShippingPriceType != ShippingPaymentType.NONE.type) {
            when (purchaseOrder.returnShippingPriceType) {
                ShippingPaymentType.EXCLUDE_REFUND_PRICE.type -> mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = true
                ShippingPaymentType.BOX.type -> mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = true
                ShippingPaymentType.DIRECT_SEND.type -> mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = true
            }
        }

        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.EXCLUDE_REFUND_PRICE.pos
                mViewModel.mRefundRequest.claimShippingPriceType = ShippingPaymentType.EXCLUDE_REFUND_PRICE.type
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
            }
        }
        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.BOX.pos
                mViewModel.mRefundRequest.claimShippingPriceType = ShippingPaymentType.BOX.type
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
            }
        }
        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.DIRECT_SEND.pos
                mViewModel.mRefundRequest.claimShippingPriceType = ShippingPaymentType.DIRECT_SEND
                        .type
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = false
            }
        }
    }

    private fun initButton() {
        mBinding.includeRequestrefundButton.cancelText = getString(R.string.common_cancel)
        mBinding.includeRequestrefundButton.confirmText = getString(R.string.requestorderstatus_refund_button_submit)
        mBinding.includeRequestrefundButton.setOnClickCancel { finish() }
        mBinding.includeRequestrefundButton.setOnClickConfirm {
            val invoiceId = mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.text.toString()
            if (invoiceId.isNotEmpty())
                mViewModel.mRefundRequest.invoiceNo = mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.text.toString().toLong()
            mViewModel.mCause = mBinding.includeRequestrefundCause.edittextRequestorderstatusCause.text.toString()
            mViewModel.requestRefund()
        }
    }

    private fun getReason(reason: String): String =
            when (reason) {
                RefundCause.CHANGE_MIND.code -> RefundCause.CHANGE_MIND.label
                RefundCause.COLOR_SIZE_CHANGE.code -> RefundCause.COLOR_SIZE_CHANGE.label
                RefundCause.MISTAKE_ORDER.code -> RefundCause.MISTAKE_ORDER.label
                RefundCause.SERVICE_UNSATISFIED.code -> RefundCause.SERVICE_UNSATISFIED.label
                RefundCause.LATE_DELIVERY.code -> RefundCause.LATE_DELIVERY.label
                RefundCause.SOLD_OUT.code -> RefundCause.SOLD_OUT.label
                RefundCause.DELIVERY_OMITTED.code -> RefundCause.DELIVERY_OMITTED.label
                RefundCause.DAMAGED.code -> RefundCause.DAMAGED.label
                RefundCause.DIFFERENT_PRODUCT.code -> RefundCause.DIFFERENT_PRODUCT.label
                RefundCause.MISTAKE_DELIVERY.code -> RefundCause.MISTAKE_DELIVERY.label
                RefundCause.DIFFERENT_OPTION.code -> RefundCause.DIFFERENT_OPTION.label
                else -> RefundCause.ETC.label
            }
}