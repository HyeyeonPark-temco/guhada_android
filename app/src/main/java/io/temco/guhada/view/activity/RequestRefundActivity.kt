package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Html
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.PurchaseStatus
import io.temco.guhada.common.enum.RefundCause
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.mypage.delivery.RequestRefundViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonSpinnerAdapter
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
    override fun onDestroy() {
        super.onDestroy()
//        if(::mViewModel.isInitialized) mViewModel.mRefundRequest.remove
    }

    override fun init() {
        initViewModel()
        initHeader()

        initExpectedRefundPrice()
    }

    private fun initHeader() {
        mBinding.includeRequestrefundHeader.title = resources.getString(R.string.requestorderstatus_refund_title)
        mBinding.includeRequestrefundHeader.setOnClickBackButton { finish() }
    }

    // 환불 예상 금액
    private fun initExpectedRefundPrice() {
        mBinding.includeRequestrefundRefund.constraintlayoutRequestcancelorderRefund.visibility = View.VISIBLE
        mViewModel.mExpectedRefundPrice.observe(this, Observer {
            mBinding.includeRequestrefundRefund.expectedRefundPrice = it
            mBinding.executePendingBindings()
        })
    }

    // 환불 계좌 정보
    private fun initBank(purchaseOrder: PurchaseOrder) {
        val bankNameList = mutableListOf<String>()
        Observable.fromIterable(purchaseOrder.banks)
                .map {
                    it.bankName
                }.subscribe {
                    bankNameList.add(it)
                }.dispose()

        bankNameList.add(getString(R.string.requestorderstatus_refund_bankhint1))
        mBinding.includeRequestrefundBank.spinnerRequestorderstatusBank.adapter = CommonSpinnerAdapter(context = this@RequestRefundActivity, layoutRes = R.layout.item_common_spinner, list = bankNameList).apply {
            this.mItemCount = bankNameList.size - 1
        }
        mBinding.includeRequestrefundBank.spinnerRequestorderstatusBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position < purchaseOrder.banks.size) {
                    val selectedBank: PurchaseOrder.Bank? = purchaseOrder.banks[position]
                    if (selectedBank != null) {
                        mBinding.includeRequestrefundBank.textviewRequestrefundBankname.text = selectedBank.bankName
                        mViewModel.mRefundRequest.refundBankCode = selectedBank.bankCode

                        mViewModel.mIsCheckAccountAvailable = ObservableBoolean(true)
                        mViewModel.notifyPropertyChanged(BR.mIsCheckAccountAvailable)
                    }
                }
            }
        }

        mViewModel.mBankAccount.observe(this, Observer {
            mBinding.includeRequestrefundBank.edittextRequestrefundBankowner.setText(it.name)
        })
        mBinding.includeRequestrefundBank.spinnerRequestorderstatusBank.setSelection(bankNameList.size - 1)
        mBinding.includeRequestrefundBank.viewModel = mViewModel
    }

    private fun initViewModel() {
        mViewModel = RequestRefundViewModel()

        // 신청서 수정
        intent.getLongExtra("orderClaimId", 0).let {
            if (it > 0 && ::mViewModel.isInitialized) {
                mViewModel.mRefundRequest.orderClaimId = it
                mViewModel.mOrderClaimId = it
                mViewModel.getUpdateClaimForm(it)
                initButton(true)
            }
        }

        // 신규 반품 신청
        intent.getLongExtra("orderProdGroupId", 0).let {
            if (it > 0 && ::mViewModel.isInitialized) {
                mViewModel.mRefundRequest.orderProdGroupId = it
                mViewModel.mOrderProdGroupId = it
                mViewModel.getClaimForm(it)
                initButton(false)
            }
        }
        mViewModel.mPurchaseOrder.observe(this, Observer {
            if (it.purchaseId > 0) {
                mBinding.includeRequestrefundRefund.constraintlayoutRequestcancelorderRefund.visibility = if (it.orderStatus == PurchaseStatus.WAITING_PAYMENT.status) View.GONE else View.VISIBLE

                initOrderInfo(it)
                initProductInfo(it)
                initCause(it)
                initSellerShipping()
                initCollection(it)
                initShippingPayment(it)
                initBank(it)

                mViewModel.mRefundRequest.quantity = it.quantity
                mViewModel.mOrderProdGroupId = it.orderProdGroupId
                mViewModel.getExpectedRefundPriceForRequest(it.quantity)
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

        mViewModel.mSuccessUpdateRefundTask = {
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
        mBinding.includeRequestrefundProductinfo.price = purchaseOrder.originalPrice
        mBinding.includeRequestrefundProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText
    }

    private fun initCause(purchaseOrder: PurchaseOrder) {
        if (!purchaseOrder.returnReason.isNullOrEmpty()) {
            mViewModel.mRefundRequest.refundReason = purchaseOrder.returnReason
            mBinding.includeRequestrefundCause.defaultMessage = getReason(purchaseOrder.returnReason)
            mBinding.includeRequestrefundCause.textviewRequestorderstatusCause.text = getReason(purchaseOrder.returnReason)
        }
        mBinding.includeRequestrefundCause.edittextRequestorderstatusCause.setText(purchaseOrder.returnReasonDetail)
        mBinding.includeRequestrefundCause.hintMessage = resources.getString(R.string.requestorderstatus_refund_hint_cause)
        mBinding.includeRequestrefundCause.quantityTitle = resources.getString(R.string.requestorderstatus_refund_quantity)
        mBinding.includeRequestrefundCause.quantity = purchaseOrder.quantity
        mBinding.includeRequestrefundCause.requestType = 2
        mBinding.includeRequestrefundCause.setOnClickAmountMinus {
//            val quantity = mBinding.includeRequestrefundCause.quantity ?: 0
            val quantity = mViewModel.mRefundRequest.quantity
            if (quantity - 1 <= 0) ToastUtil.showMessage("반품 가능 최소 수량 1개")
            else {
                mBinding.includeRequestrefundCause.quantity = quantity - 1
                mViewModel.mRefundRequest.quantity = quantity - 1
                mViewModel.getExpectedRefundPriceForRequest(quantity - 1)
            }
        }
        mBinding.includeRequestrefundCause.setOnClickAmountPlus {
//            val quantity = mBinding.includeRequestrefundCause.quantity ?: 0
            val quantity = mViewModel.mRefundRequest.quantity
            if (quantity + 1 > purchaseOrder.quantity) ToastUtil.showMessage("반품 가능 최대 수량 ${purchaseOrder.quantity}개")
            else {
                mBinding.includeRequestrefundCause.quantity = quantity + 1
                mViewModel.mRefundRequest.quantity = quantity + 1
                mViewModel.getExpectedRefundPriceForRequest(quantity + 1)
            }
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
                        mViewModel.mSelectedShippingPayment = cause
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

                // 택배사 및 송장번호 초기화
                mViewModel.mRefundRequest.shippingCompanyCode = ""
                mViewModel.mRefundRequest.shippingCompanyName = ""
                mViewModel.mRefundRequest.invoiceNo = 0L
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

        for (reason in purchaseOrder.returnReasonList ?: mutableListOf()) {
            val returnReason = purchaseOrder.returnReason
            if (returnReason == reason.code && !reason.isFeeCharged) {
                mBinding.includeRequestrefundShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility = View.GONE
                break
            }
        }

        // [신청서 수정] 배송비 결제 방법
        setShippingPayment(purchaseOrder)
        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mRefundRequest.claimShippingPriceType = ShippingPaymentType.EXCLUDE_REFUND_PRICE.type
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
            }
        }
        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mRefundRequest.claimShippingPriceType = ShippingPaymentType.BOX.type
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
            }
        }
        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mRefundRequest.claimShippingPriceType = ShippingPaymentType.DIRECT_SEND.type
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = false
            }
        }

    }

    private fun initButton(isModify: Boolean) {
        mBinding.includeRequestrefundButton.cancelText = getString(R.string.common_cancel)
        mBinding.includeRequestrefundButton.confirmText = if (isModify) getString(R.string.requestorderstatus_refund_button_update) else getString(R.string.requestorderstatus_refund_button_submit)
        mBinding.includeRequestrefundButton.setOnClickCancel { finish() }
        mBinding.includeRequestrefundButton.setOnClickConfirm {
            val invoiceId = mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.text.toString()
            if (invoiceId.isNotEmpty())
                mViewModel.mRefundRequest.invoiceNo = mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.text.toString().toLong()
            mViewModel.mRefundRequest.refundReasonDetail = mBinding.includeRequestrefundCause.edittextRequestorderstatusCause.text.toString()

            if (isModify) mViewModel.updateRefund()
            else mViewModel.requestRefund()
        }
    }

    private fun setShippingPayment(purchaseOrder: PurchaseOrder) {
        if (purchaseOrder.returnShippingPriceType != null && purchaseOrder.returnShippingPriceType != ShippingPaymentType.NONE.type) {
            when (purchaseOrder.returnShippingPriceType) {
                ShippingPaymentType.EXCLUDE_REFUND_PRICE.type -> mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = true
                ShippingPaymentType.BOX.type -> mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = true
                ShippingPaymentType.DIRECT_SEND.type -> mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = true
            }
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