package io.temco.guhada.view.activity

import android.text.Html
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.ExchangeRequest
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.mypage.delivery.RequestExchangeViewModel
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
        initOrderInfo()
        initProductInfo()
        initCause()
        initSellerShipping()
        initCollection()
        initShippingPayment()
        initBank()
        initButton()
    }

    // TODO 환불 계좌 정보
    private fun initBank() {

    }

    private fun initViewModel() {
        mViewModel = RequestRefundViewModel()
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null) mViewModel.mPurchaseOrder = it as PurchaseOrder
        }
        intent.getSerializableExtra("exchangeRequest").let {
            if (it != null) mViewModel.mExchangeRequest = it as ExchangeRequest
        }
    }

    private fun initOrderInfo() {
        mBinding.includeRequestrefundOrderinfo.orderNumber = mViewModel.mPurchaseOrder.purchaseId.toInt()
        mBinding.includeRequestrefundOrderinfo.orderTimeStamp = mViewModel.mPurchaseOrder.orderTimestamp
    }

    private fun initProductInfo() {
        mBinding.includeRequestrefundProductinfo.imageUrl = mViewModel.mPurchaseOrder.imageUrl
        mBinding.includeRequestrefundProductinfo.brandName = mViewModel.mPurchaseOrder.brandName
        mBinding.includeRequestrefundProductinfo.productName = "${mViewModel.mPurchaseOrder.season} ${mViewModel.mPurchaseOrder.dealName}"
        mBinding.includeRequestrefundProductinfo.optionStr = mViewModel.mPurchaseOrder.getOptionStr()
        mBinding.includeRequestrefundProductinfo.price = mViewModel.mPurchaseOrder.discountPrice
        mBinding.includeRequestrefundProductinfo.purchaseStatusText = mViewModel.mPurchaseOrder.purchaseStatusText
    }

    private fun initCause() {
        mBinding.includeRequestrefundCause.defaultMessage = resources.getString(R.string.requestorderstatus_refund_cause)
        mBinding.includeRequestrefundCause.hintMessage = resources.getString(R.string.requestorderstatus_refund_hint_cause)
        mBinding.includeRequestrefundCause.quantityTitle = resources.getString(R.string.requestorderstatus_refund_quantity)
        mBinding.includeRequestrefundCause.quantity = 1
        mBinding.includeRequestrefundCause.setOnClickAmountMinus {
            val quantity = mBinding.includeRequestrefundCause.quantity ?: 0
            if (quantity - 1 <= 0) ToastUtil.showMessage("반품 가능 최소 수량 1개")
            else mBinding.includeRequestrefundCause.quantity = quantity - 1

            mViewModel.mExchangeRequest.quantity = mBinding.includeRequestrefundCause.quantity
                    ?: 0
        }
        mBinding.includeRequestrefundCause.setOnClickAmountPlus {
            val quantity = mBinding.includeRequestrefundCause.quantity ?: 0
            if (quantity + 1 > mViewModel.mPurchaseOrder.quantity) ToastUtil.showMessage("반품 가능 최대 수량 ${mViewModel.mPurchaseOrder.quantity}개")
            else mBinding.includeRequestrefundCause.quantity = quantity + 1

            mViewModel.mExchangeRequest.quantity = mBinding.includeRequestrefundCause.quantity
                    ?: 0
        }
        mBinding.includeRequestrefundCause.causeList = mViewModel.mCauseList
        mBinding.includeRequestrefundCause.spinnerRequestorderstatusCause.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val cause = mViewModel.mCauseList[position]
                mViewModel.mExchangeRequest.exchangeReason = cause.code
                mBinding.includeRequestrefundCause.defaultMessage = cause.label

                mBinding.includeRequestrefundShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility =
                        if (cause.isFeeCharged) View.VISIBLE
                        else View.GONE

                if (!cause.isFeeCharged) mViewModel.mExchangeRequest.claimShippingPriceType = ShippingPaymentType.NONE.type
            }
        }
        mBinding.includeRequestrefundCause.edittextRequestorderstatusCause.addTextChangedListener {
            mViewModel.mExchangeRequest.exchangeReasonDetail = it.toString()
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

    private fun initCollection() {
        mBinding.includeRequestrefundCollection.title = resources.getString(R.string.requestorderstatus_exchange_way_title)
        mBinding.includeRequestrefundCollection.description = resources.getString(R.string.requestorderstatus_exchange_way_description)
        mBinding.includeRequestrefundCollection.radiobuttonRequestorderstatusWayTrue.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mExchangeRequest.alreadySend = true
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
                mViewModel.mExchangeRequest.alreadySend = false
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
                    if (company != null) mViewModel.mExchangeRequest.shippingCompanyCode = company.code
                    mBinding.includeRequestrefundCollection.textviewRequestorderstatusShippingcompany.text = company?.name
                }
            }
            mBinding.includeRequestrefundCollection.spinnerRequestorderstatusShippingcompany.setSelection(it.size - 1)
        })

        mBinding.includeRequestrefundCollection.edittextRequestorderstatusShippingid.addTextChangedListener {
            mBinding.includeRequestrefundCollection.textviewRequestorderstatusWarning.visibility = if (it.isNullOrEmpty()) View.VISIBLE
            else View.GONE
        }

        mViewModel.getShippingCompany()
    }

    private fun initShippingPayment() {
        mBinding.includeRequestrefundShippingpayment.title = resources.getString(R.string.requestorderstatus_refund_shipping_title)
        mBinding.includeRequestrefundShippingpayment.textviewRequestorderstatusShippingpaymentDescription1.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_refund_shipping_description1))
        mBinding.includeRequestrefundShippingpayment.textviewRequestorderstatusShippingpaymentDescription2.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_refund_shipping_description2))
        mBinding.includeRequestrefundShippingpayment.isRefund = true

        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.EXCLUDE_REFUND_PRICE.pos
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
            }
        }
        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.BOX.pos
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = false
                mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
            }
        }
        mBinding.includeRequestrefundShippingpayment.radiobuttonRequestorderstatusShippingpayment3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.DIRECT_SEND.pos
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
            mViewModel.mCause = mBinding.includeRequestrefundCause.edittextRequestorderstatusCause.text.toString()
            mViewModel.requestRefund()
        }
    }
}