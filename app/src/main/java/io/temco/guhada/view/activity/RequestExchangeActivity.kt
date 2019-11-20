package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.ExchangeCause
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.enum.ShippingMessageCode
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.mypage.delivery.RequestExchangeViewModel
import io.temco.guhada.databinding.ActivityRequestexchangeBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ShippingCompanySpinnerAdapter
import io.temco.guhada.view.adapter.payment.PaymentSpinnerAdapter

/**
 * 교환 신청
 * @author Hyeyeon Park
 * @since 2019.08.16
 */
class RequestExchangeActivity : BindActivity<ActivityRequestexchangeBinding>() {
    private lateinit var mViewModel: RequestExchangeViewModel

    override fun getBaseTag(): String = RequestExchangeActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_requestexchange

    override fun getViewType(): Type.View = Type.View.REQUEST_EXCHANGE

    override fun init() {
        initViewModel()
        initHeader()

        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = RequestExchangeViewModel()

        // 신청서 수정
        intent.getLongExtra("orderClaimId", 0).let {
            if (it > 0 && ::mViewModel.isInitialized) {
                mViewModel.mExchangeRequest.orderClaimId = it
                mViewModel.mOrderClaimId = it
                mViewModel.getUpdateClaimForm(it)
                initButton(true)
            }
        }

        // 신규 교환 신청
        intent.getLongExtra("orderProdGroupId", 0).let {
            if (it > 0 && ::mViewModel.isInitialized) {
                mViewModel.mExchangeRequest.orderProdGroupId = it
                mViewModel.mOrderProdGroupId = it
                mViewModel.getClaimForm(it)
                initButton(false)
            }
        }
        mViewModel.mPurchaseOrder.observe(this, Observer {
            if (it.purchaseId > 0) {
                initOrderInfo()
                initProductInfo()
                initCause(it)
                initSellerShipping()
                initCollection(it)
                initShippingPayment(it)
                initExchangeShipping(it)

                mViewModel.mExchangeRequest.quantity = it.quantity
                mBinding.executePendingBindings()
            }
        })

        mViewModel.mSuccessRequestExchangeTask = { purchaseOrder ->
            val intent = Intent(this@RequestExchangeActivity, SuccessRequestExchangeActivity::class.java)
            intent.putExtra("purchaseOrder", purchaseOrder)
            intent.putExtra("seller", mViewModel.mSeller.value)
            intent.putExtra("sellerAddress", mViewModel.mSellerAddress.value)
            intent.putExtra("exchangeRequest", mViewModel.mExchangeRequest)

            startActivity(intent)
            setResult(Activity.RESULT_OK)
            finish()
        }

        mViewModel.mSuccessUpdateExchangeTask = {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun initHeader() {
        mBinding.includeRequestexchangeHeader.title = resources.getString(R.string.requestorderstatus_exchange_title)
        mBinding.includeRequestexchangeHeader.setOnClickBackButton { finish() }
    }

    private fun initOrderInfo() {
        mBinding.includeRequestexchangeOrderinfo.orderNumber = mViewModel.mPurchaseOrder.value?.purchaseId?.toInt()
        mBinding.includeRequestexchangeOrderinfo.orderTimeStamp = mViewModel.mPurchaseOrder.value?.orderTimestamp
    }

    private fun initProductInfo() {
        mBinding.includeRequestexchangeProductinfo.imageUrl = mViewModel.mPurchaseOrder.value?.imageUrl
        mBinding.includeRequestexchangeProductinfo.brandName = mViewModel.mPurchaseOrder.value?.brandName
        mBinding.includeRequestexchangeProductinfo.productName =
                if (mViewModel.mPurchaseOrder.value?.season.isNullOrEmpty()) mViewModel.mPurchaseOrder.value?.productName
                else "${mViewModel.mPurchaseOrder.value?.season} ${mViewModel.mPurchaseOrder.value?.productName}"
        mBinding.includeRequestexchangeProductinfo.optionStr = mViewModel.mPurchaseOrder.value?.getOptionStr()
        mBinding.includeRequestexchangeProductinfo.price = mViewModel.mPurchaseOrder.value?.originalPrice
        mBinding.includeRequestexchangeProductinfo.purchaseStatusText = mViewModel.mPurchaseOrder.value?.purchaseStatusText
    }

    private fun initCause(purchaseOrder: PurchaseOrder) {
        if (!purchaseOrder.exchangeReason.isNullOrEmpty()) {
            mViewModel.mExchangeRequest.exchangeReason = purchaseOrder.exchangeReason
            mBinding.includeRequestexchangeCause.defaultMessage = getReason(purchaseOrder.exchangeReason)
            mBinding.includeRequestexchangeCause.textviewRequestorderstatusCause.text = getReason(purchaseOrder.exchangeReason)
        } else {
            mBinding.includeRequestexchangeCause.textviewRequestorderstatusCause.text = getString(R.string.requestorderstatus_exchange_cause)
            mBinding.includeRequestexchangeCause.defaultMessage = getString(R.string.requestorderstatus_exchange_cause)
        }

        mBinding.includeRequestexchangeCause.hintMessage = resources.getString(R.string.requestorderstatus_exchange_hint_cause)
        mBinding.includeRequestexchangeCause.edittextRequestorderstatusCause.setText(purchaseOrder.exchangeReasonDetail)
        mBinding.includeRequestexchangeCause.quantityTitle = resources.getString(R.string.requestorderstatus_exchange_quantity)
        mBinding.includeRequestexchangeCause.quantity = purchaseOrder.quantity
        mBinding.includeRequestexchangeCause.requestType = 1

        mBinding.includeRequestexchangeCause.setOnClickAmountMinus {
            val quantity = mViewModel.mExchangeRequest.quantity
            if (quantity - 1 <= 0)
                ToastUtil.showMessage("교환 가능 최소 수량 1개")
            else mBinding.includeRequestexchangeCause.quantity = quantity - 1

            mViewModel.mExchangeRequest.quantity = mBinding.includeRequestexchangeCause.quantity
                    ?: 0
        }
        mBinding.includeRequestexchangeCause.setOnClickAmountPlus {
            val quantity = mViewModel.mExchangeRequest.quantity
            if (quantity + 1 > purchaseOrder.quantity) ToastUtil.showMessage("교환 가능 최대 수량 ${purchaseOrder.quantity}개")
            else mBinding.includeRequestexchangeCause.quantity = quantity + 1

            mViewModel.mExchangeRequest.quantity = mBinding.includeRequestexchangeCause.quantity
                    ?: 0
        }
        mBinding.includeRequestexchangeCause.causeList = purchaseOrder.exchangeReasonList
        mBinding.includeRequestexchangeCause.spinnerRequestorderstatusCause.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (purchaseOrder.exchangeReasonList != null) {
                    if (!purchaseOrder.exchangeReasonList.isNullOrEmpty() && position > (purchaseOrder.exchangeReasonList!!.size - 2)) {

                    } else {
                        val cause = mViewModel.mPurchaseOrder.value?.exchangeReasonList!![position]
                        mViewModel.mSelectedShippingPayment = cause
                        mViewModel.mExchangeRequest.exchangeReason = cause.code
                        mBinding.includeRequestexchangeCause.defaultMessage = cause.label

                        if (purchaseOrder.exchangeShippingPrice > 0 || purchaseOrder.exchangeShipExpense > 0)
                            mBinding.includeRequestexchangeShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility =
                                    if (cause.userFault) View.VISIBLE
                                    else View.GONE

                        if (!cause.userFault)
                            mViewModel.mExchangeRequest.claimShippingPriceType = ShippingPaymentType.NONE.type
                    }
                }
            }
        }
        mBinding.includeRequestexchangeCause.edittextRequestorderstatusCause.addTextChangedListener {
            mViewModel.mExchangeRequest.exchangeReasonDetail = it.toString()
        }

        CommonUtil.hideKeyboard(this@RequestExchangeActivity, mBinding.includeRequestexchangeCause.edittextRequestorderstatusCause)
    }


    private fun initSellerShipping() {
        mBinding.includeRequestexchangeSellershipping.title = resources.getString(R.string.requestorderstatus_exchange_selleraddress)
        mViewModel.mSeller.observe(this, Observer {
            mBinding.includeRequestexchangeSellershipping.sellerName = mViewModel.mSeller.value?.storeName
            mBinding.includeRequestexchangeSellershipping.sellerPhone = mViewModel.mSeller.value?.claimTelephone
            mBinding.includeRequestexchangeCause.sellerName = mViewModel.mSeller.value?.storeName
        })
        mViewModel.mSellerAddress.observe(this, Observer {
            mBinding.includeRequestexchangeSellershipping.address = "[${it.zip}] ${it.roadAddress} ${it.detailAddress}"
        })

        mViewModel.getSellerInfo()
        mViewModel.getSellerDefaultReturnAddress()
    }

    private fun initCollection(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestexchangeCollection.title = resources.getString(R.string.requestorderstatus_exchange_way_title)
        mBinding.includeRequestexchangeCollection.description = resources.getString(R.string.requestorderstatus_exchange_way_description)

        // [신청서 수정] 택배사
        if (!purchaseOrder.exchangePickingShipCompany.isNullOrEmpty()) {
            mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayTrue.isChecked = true
            mBinding.includeRequestexchangeCollection.textviewRequestorderstatusShippingcompany.text = purchaseOrder.exchangePickingShipCompany

            mBinding.includeRequestexchangeCollection.framelayoutRequestorderstatusShippingcompany.visibility = View.VISIBLE
            mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.visibility = View.VISIBLE

            if (!purchaseOrder.exchangePickingInvoiceNo.isNullOrEmpty()) {
                mBinding.includeRequestexchangeCollection.imageviewRequestorderstatusWarning.visibility = View.GONE
                mBinding.includeRequestexchangeCollection.textviewRequestorderstatusWarning.visibility = View.GONE
                mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.setText(purchaseOrder.exchangePickingInvoiceNo)
            }
        }else {
            mViewModel.mExchangeRequest.alreadySend = false
            mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayFalse.isChecked = true
            mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayTrue.isChecked = false

            // 택배사 및 송장번호 입력 란
            mBinding.includeRequestexchangeCollection.framelayoutRequestorderstatusShippingcompany.visibility = View.GONE
            mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.visibility = View.GONE
            mBinding.includeRequestexchangeCollection.textviewRequestorderstatusWarning.visibility = View.GONE
            mBinding.includeRequestexchangeCollection.imageviewRequestorderstatusWarning.visibility = View.GONE

            // 택배사 및 송장번호 초기화
            mViewModel.mExchangeRequest.shippingCompanyCode = ""
            mViewModel.mExchangeRequest.shippingCompanyName = ""
            mViewModel.mExchangeRequest.invoiceNo = 0L
        }

        // LISTENER
        mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayTrue.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mExchangeRequest.alreadySend = true
                mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayFalse.isChecked = false

                // 택배사 및 송장번호 입력 란
                mBinding.includeRequestexchangeCollection.framelayoutRequestorderstatusShippingcompany.visibility = View.VISIBLE
                mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.visibility = View.VISIBLE
                mBinding.includeRequestexchangeCollection.textviewRequestorderstatusWarning.visibility = View.VISIBLE
                mBinding.includeRequestexchangeCollection.imageviewRequestorderstatusWarning.visibility = View.VISIBLE
            }
        }
        mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayFalse.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mExchangeRequest.alreadySend = false
                mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayTrue.isChecked = false

                // 택배사 및 송장번호 입력 란
                mBinding.includeRequestexchangeCollection.framelayoutRequestorderstatusShippingcompany.visibility = View.GONE
                mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.visibility = View.GONE
                mBinding.includeRequestexchangeCollection.textviewRequestorderstatusWarning.visibility = View.GONE
                mBinding.includeRequestexchangeCollection.imageviewRequestorderstatusWarning.visibility = View.GONE

                // 택배사 및 송장번호 초기화
                mViewModel.mExchangeRequest.shippingCompanyCode = ""
                mViewModel.mExchangeRequest.shippingCompanyName = ""
                mViewModel.mExchangeRequest.invoiceNo = 0L
            }
        }

        mViewModel.mShippingCompanyList.observe(this, Observer {
            mBinding.includeRequestexchangeCollection.spinnerRequestorderstatusShippingcompany.adapter = ShippingCompanySpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_payment_spinner, it)
            mBinding.includeRequestexchangeCollection.spinnerRequestorderstatusShippingcompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val company = mViewModel.mShippingCompanyList.value?.get(position)
                    if (company != null) {
                        mViewModel.mExchangeRequest.shippingCompanyCode = company.code
                        mViewModel.mExchangeRequest.shippingCompanyName = company.name
                    }
                    mBinding.includeRequestexchangeCollection.textviewRequestorderstatusShippingcompany.text = company?.name
                }
            }

            if (!purchaseOrder.exchangePickingShipCompany.isNullOrEmpty()) { // 신청서 수정
                val code = purchaseOrder.exchangePickingShipCompany
                for (i in 0 until it.size) {
                    if (code == it[i].code) {
                        mBinding.includeRequestexchangeCollection.spinnerRequestorderstatusShippingcompany.setSelection(i)
                        mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayTrue.isChecked = true
                        mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.setText(
                                if (purchaseOrder.exchangePickingInvoiceNo == "0") ""
                                else purchaseOrder.exchangePickingInvoiceNo)
                        break
                    }
                }
            } else {
                mBinding.includeRequestexchangeCollection.spinnerRequestorderstatusShippingcompany.setSelection(it.size - 1)
                mBinding.includeRequestexchangeCollection.radiobuttonRequestorderstatusWayFalse.isChecked = true
            }
        })

        mViewModel.getShippingCompany()
    }

    private fun initShippingPayment(purchaseOrder: PurchaseOrder) {
        if (purchaseOrder.exchangeShipExpense > 0 || purchaseOrder.exchangeShippingPrice > 0) {
            mBinding.includeRequestexchangeShippingpayment.title = resources.getString(R.string.requestorderstatus_exchange_shipping_title)

            val exchangeShippingPrice: Int =
                    if (purchaseOrder.exchangeShipExpense > 0) purchaseOrder.exchangeShipExpense
                    else if (purchaseOrder.exchangeShippingPrice > 0) purchaseOrder.exchangeShippingPrice
                    else 0

            mBinding.includeRequestexchangeShippingpayment.textviewRequestorderstatusShippingpaymentDescription1.text = Html.fromHtml(String.format(resources.getString(R.string.requestorderstatus_exchange_shipping_description1), exchangeShippingPrice))
            mBinding.includeRequestexchangeShippingpayment.textviewRequestorderstatusShippingpaymentDescription2.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_exchange_shipping_description2))

            for (reason in purchaseOrder.exchangeReasonList ?: mutableListOf()) {
                val returnReason = purchaseOrder.exchangeReason
                if (returnReason == reason.code && !reason.userFault) {
                    mBinding.includeRequestexchangeShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility = View.GONE
                    break
                }
            }

            // [신청서 수정] 배송비 결제 방법
            if (purchaseOrder.exchangeShippingPriceType != null && purchaseOrder.exchangeShippingPriceType != ShippingPaymentType.NONE.type) {
                when (purchaseOrder.exchangeShippingPriceType) {
                    ShippingPaymentType.BOX.type -> mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = true
                    ShippingPaymentType.DIRECT_SEND.type -> mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = true
                }
            }

            mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mViewModel.mShippingPayment = ShippingPaymentType.BOX.pos
                    mViewModel.mExchangeRequest.claimShippingPriceType = ShippingPaymentType.BOX.type
                    mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
                }
            }
            mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment3.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mViewModel.mShippingPayment = ShippingPaymentType.DIRECT_SEND.pos
                    mViewModel.mExchangeRequest.claimShippingPriceType = ShippingPaymentType.DIRECT_SEND.type
                    mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = false
                }
            }
        } else {
            mBinding.includeRequestexchangeShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility = View.GONE
        }

        setShippingPayment(purchaseOrder)
    }

    private fun setShippingPayment(purchaseOrder: PurchaseOrder) {
        if (!TextUtils.isEmpty(purchaseOrder.returnShippingPriceType) && purchaseOrder.returnShippingPriceType != ShippingPaymentType.NONE.type) {
            when (purchaseOrder.returnShippingPriceType) {
                ShippingPaymentType.EXCLUDE_REFUND_PRICE.type -> mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment1.isChecked = true
                ShippingPaymentType.BOX.type -> mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = true
                ShippingPaymentType.DIRECT_SEND.type -> mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = true
            }
        }
    }

    private fun initExchangeShipping(purchaseOrder: PurchaseOrder) {
        mBinding.includeRequestexchangeExchangeshipping.title = resources.getString(R.string.requestorderstatus_exchange_address)
        mBinding.includeRequestexchangeExchangeshipping.spinnerRequestorderstatusShippingmemo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (TextUtils.isEmpty(purchaseOrder.receiverMessage) && position > (purchaseOrder.shippingMessageList.size - 2)) {

                } else {
                    val shippingMessage = mViewModel.mShippingMessageList.value?.get(position)
                    mBinding.includeRequestexchangeExchangeshipping.textviewRequestorderstatusShippingmemo.text = shippingMessage?.message

                    if (shippingMessage?.type == ShippingMessageCode.SELF.code) {
                        mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.visibility = View.VISIBLE
                        mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.addTextChangedListener {
                            mViewModel.mExchangeRequest.exchangeShippingAddress.shippingMessage = it.toString()
                        }
                    } else {
                        mViewModel.mExchangeRequest.exchangeShippingAddress.shippingMessage = shippingMessage?.message
                                ?: ""
                        mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.removeTextChangedListener(null)
                        mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.visibility = View.GONE
                    }
                }
            }
        }

        // 교환상품 배송지
        if(purchaseOrder.exchangeBuyerAddress.isNullOrEmpty()){
            mBinding.includeRequestexchangeExchangeshipping.name = purchaseOrder.receiverAddressName
            mBinding.includeRequestexchangeExchangeshipping.address = "[${purchaseOrder.receiverZipcode}] ${purchaseOrder.receiverRoadAddress} ${purchaseOrder.receiverAddressDetail}"
            mBinding.includeRequestexchangeExchangeshipping.defaultAddress = false
            mViewModel.mExchangeRequest.exchangeShippingAddress.shippingName = purchaseOrder.receiverAddressName
            mViewModel.mExchangeRequest.exchangeShippingAddress.zip = purchaseOrder.receiverZipcode
            mViewModel.mExchangeRequest.exchangeShippingAddress.address = purchaseOrder.receiverRoadAddress
            mViewModel.mExchangeRequest.exchangeShippingAddress.roadAddress = purchaseOrder.receiverRoadAddress
            mViewModel.mExchangeRequest.exchangeShippingAddress.detailAddress = purchaseOrder.receiverAddressDetail
            mViewModel.mExchangeRequest.exchangeShippingAddress.recipientName = purchaseOrder.receiverName
            mViewModel.mExchangeRequest.exchangeShippingAddress.recipientMobile = purchaseOrder.receiverPhone
        }else {
            mBinding.includeRequestexchangeExchangeshipping.name = purchaseOrder.exchangeBuyerAddressName
            mBinding.includeRequestexchangeExchangeshipping.address = "[${purchaseOrder.exchangeBuyerZip}] ${purchaseOrder.exchangeBuyerRoadAddress} ${purchaseOrder.exchangeBuyerDetailAddress}"
            mBinding.includeRequestexchangeExchangeshipping.defaultAddress = false
            mViewModel.mExchangeRequest.exchangeShippingAddress.shippingName = purchaseOrder.exchangeBuyerAddressName
            mViewModel.mExchangeRequest.exchangeShippingAddress.zip = purchaseOrder.exchangeBuyerZip
            mViewModel.mExchangeRequest.exchangeShippingAddress.address = purchaseOrder.exchangeBuyerAddress
            mViewModel.mExchangeRequest.exchangeShippingAddress.roadAddress = purchaseOrder.exchangeBuyerRoadAddress
            mViewModel.mExchangeRequest.exchangeShippingAddress.detailAddress = purchaseOrder.exchangeBuyerDetailAddress
            mViewModel.mExchangeRequest.exchangeShippingAddress.recipientName = purchaseOrder.exchangeBuyerRecipientName
            mViewModel.mExchangeRequest.exchangeShippingAddress.recipientMobile = purchaseOrder.exchangeBuyerRecipientMobile
        }

        // 배송지 변경
        mBinding.includeRequestexchangeExchangeshipping.setChangeShippingListener {
            val intent = Intent(this@RequestExchangeActivity, ShippingAddressActivity::class.java)
            startActivityForResult(intent, RequestCode.SHIPPING_ADDRESS.flag)
        }

        // 배송 메세지
//        for (i in 0 until purchaseOrder.shippingMessageList.size) {
//            val shippingMessage = purchaseOrder.shippingMessageList[i]
//            if (shippingMessage.message == purchaseOrder.exchangeBuyerShippingMessage) {
//                mBinding.includeRequestexchangeExchangeshipping.textviewRequestorderstatusShippingmemo.text = purchaseOrder.exchangeBuyerShippingMessage
//                        ?: ""
//                mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.visibility = View.GONE
//                break
//            } else if (i == purchaseOrder.shippingMessageList.size - 1) {
//                mBinding.includeRequestexchangeExchangeshipping.textviewRequestorderstatusShippingmemo.text = BaseApplication.getInstance().getString(R.string.shippingmemo_self)
//                mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.setText(purchaseOrder.exchangeBuyerShippingMessage
//                        ?: "")
//                mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.visibility = View.VISIBLE
//            }
//        }

        // 배송 메세지 리스트
        mViewModel.mShippingMessageList.observe(this, Observer {
            mBinding.includeRequestexchangeExchangeshipping.spinnerRequestorderstatusShippingmemo.adapter = PaymentSpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_payment_spinner, it)
            if (it.isNotEmpty()) {
                if (!purchaseOrder.exchangeBuyerShippingMessage.isNullOrEmpty()) {
                    for (i in 0 until it.size)
                        if (it[i].message == purchaseOrder.exchangeBuyerShippingMessage){
                            mBinding.includeRequestexchangeExchangeshipping.spinnerRequestorderstatusShippingmemo.setSelection(i)
                            break
                        }
                } else {
                    mBinding.includeRequestexchangeExchangeshipping.spinnerRequestorderstatusShippingmemo.setSelection(it.size - 1)
                }
            }
        })
        // mViewModel.getShippingMessageByOrderForm()
    }

    private fun initButton(isModify: Boolean) {
        mBinding.includeRequestexchangeButton.cancelText = getString(R.string.common_cancel)
        mBinding.includeRequestexchangeButton.confirmText = if (isModify) getString(R.string.requestorderstatus_exchange_button_update) else getString(R.string.requestorderstatus_exchange_button_submit)
        mBinding.includeRequestexchangeButton.setOnClickConfirm {
            val invoiceId = mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.text.toString()
            if (invoiceId.isNotEmpty())
                mViewModel.mExchangeRequest.invoiceNo = mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.text.toString().toLong()
            mViewModel.mExchangeRequest.exchangeReasonDetail = mBinding.includeRequestexchangeCause.edittextRequestorderstatusCause.text.toString()

            if (isModify) mViewModel.updateExchange()
            else mViewModel.requestExchange()
        }
        mBinding.includeRequestexchangeButton.setOnClickCancel { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.SHIPPING_ADDRESS.flag && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra("shippingAddress").let {
                if (it != null) {
                    val shippingAddress = it as UserShipping
                    val shippingMessage = mViewModel.mExchangeRequest.exchangeShippingAddress.shippingMessage
                    mViewModel.mExchangeRequest.exchangeShippingAddress = shippingAddress.apply { this.shippingMessage = shippingMessage }
                    mBinding.includeRequestexchangeExchangeshipping.address = "[${shippingAddress.zip}] ${shippingAddress.roadAddress} ${shippingAddress.detailAddress}"
                    mBinding.includeRequestexchangeExchangeshipping.defaultAddress = shippingAddress.defaultAddress
                    mBinding.includeRequestexchangeExchangeshipping.name = shippingAddress.shippingName
                }
            }
        }
    }

    private fun getReason(reason: String): String =
            when (reason) {
                ExchangeCause.COLOR_SIZE_CHANGE.code -> ExchangeCause.COLOR_SIZE_CHANGE.label
                ExchangeCause.MISTAKE_ORDER.code -> ExchangeCause.MISTAKE_ORDER.label
                ExchangeCause.SERVICE_UNSATISFIED.code -> ExchangeCause.SERVICE_UNSATISFIED.label
                ExchangeCause.LATE_DELIVERY.code -> ExchangeCause.LATE_DELIVERY.label
                ExchangeCause.SOLD_OUT.code -> ExchangeCause.SOLD_OUT.label
                ExchangeCause.DELIVERY_OMITTED.code -> ExchangeCause.DELIVERY_OMITTED.label
                ExchangeCause.DAMAGED.code -> ExchangeCause.DAMAGED.label
                ExchangeCause.DIFFERENT_PRODUCT.code -> ExchangeCause.DIFFERENT_PRODUCT.label
                ExchangeCause.MISTAKE_DELIVERY.code -> ExchangeCause.MISTAKE_DELIVERY.label
                ExchangeCause.DIFFERENT_OPTION.code -> ExchangeCause.DIFFERENT_OPTION.label
                else -> ExchangeCause.ETC.label
            }
}