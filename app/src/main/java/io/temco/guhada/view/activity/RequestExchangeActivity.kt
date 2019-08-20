package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Html
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.enum.ShippingMessageCode
import io.temco.guhada.common.enum.ShippingPaymentType
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
        initOrderInfo()
        initProductInfo()
        initCause()
        initSellerShipping()
        initCollection()
        initShippingPayment()
        initExchangeShipping()
        initButton()
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = RequestExchangeViewModel()
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null) {
                mViewModel.mPurchaseOrder = it as PurchaseOrder
                mViewModel.mExchangeRequest.orderProdGroupId = it.orderProdGroupId
            }
        }
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
    }

    private fun initHeader() {
        mBinding.includeRequestexchangeHeader.title = resources.getString(R.string.requestorderstatus_exchange_title)
        mBinding.includeRequestexchangeHeader.setOnClickBackButton { finish() }
    }

    private fun initOrderInfo() {
        mBinding.includeRequestexchangeOrderinfo.orderNumber = mViewModel.mPurchaseOrder.purchaseId.toInt()
        mBinding.includeRequestexchangeOrderinfo.orderTimeStamp = mViewModel.mPurchaseOrder.orderTimestamp
    }

    private fun initProductInfo() {
        mBinding.includeRequestexchangeProductinfo.imageUrl = mViewModel.mPurchaseOrder.imageUrl
        mBinding.includeRequestexchangeProductinfo.brandName = mViewModel.mPurchaseOrder.brandName
        mBinding.includeRequestexchangeProductinfo.productName = "${mViewModel.mPurchaseOrder.season} ${mViewModel.mPurchaseOrder.dealName}"
        mBinding.includeRequestexchangeProductinfo.optionStr = mViewModel.mPurchaseOrder.getOptionStr()
        mBinding.includeRequestexchangeProductinfo.price = mViewModel.mPurchaseOrder.discountPrice
        mBinding.includeRequestexchangeProductinfo.purchaseStatusText = mViewModel.mPurchaseOrder.purchaseStatusText
    }

    private fun initCause() {
        mBinding.includeRequestexchangeCause.defaultMessage = resources.getString(R.string.requestorderstatus_exchange_cause)
        mBinding.includeRequestexchangeCause.hintMessage = resources.getString(R.string.requestorderstatus_exchange_hint_cause)
        mBinding.includeRequestexchangeCause.quantityTitle = resources.getString(R.string.requestorderstatus_exchange_quantity)
        mBinding.includeRequestexchangeCause.quantity = 1
        mBinding.includeRequestexchangeCause.requestType = 1

        mBinding.includeRequestexchangeCause.setOnClickAmountMinus {
            val quantity = mBinding.includeRequestexchangeCause.quantity ?: 0
            if (quantity - 1 <= 0)
                ToastUtil.showMessage("교환 가능 최소 수량 1개")
            else mBinding.includeRequestexchangeCause.quantity = quantity - 1

            mViewModel.mExchangeRequest.quantity = mBinding.includeRequestexchangeCause.quantity
                    ?: 0
        }
        mBinding.includeRequestexchangeCause.setOnClickAmountPlus {
            val quantity = mBinding.includeRequestexchangeCause.quantity ?: 0
            if (quantity + 1 > mViewModel.mPurchaseOrder.quantity) ToastUtil.showMessage("교환 가능 최대 수량 ${mViewModel.mPurchaseOrder.quantity}개")
            else mBinding.includeRequestexchangeCause.quantity = quantity + 1

            mViewModel.mExchangeRequest.quantity = mBinding.includeRequestexchangeCause.quantity
                    ?: 0
        }
        mBinding.includeRequestexchangeCause.causeList = mViewModel.mCauseList
        mBinding.includeRequestexchangeCause.spinnerRequestorderstatusCause.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val cause = mViewModel.mCauseList[position]
                mViewModel.mExchangeRequest.exchangeReason = cause.code
                mBinding.includeRequestexchangeCause.defaultMessage = cause.label

                mBinding.includeRequestexchangeShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility =
                        if (cause.isFeeCharged) View.VISIBLE
                        else View.GONE

                if (!cause.isFeeCharged) mViewModel.mExchangeRequest.claimShippingPriceType = ShippingPaymentType.NONE.type
            }
        }
//        mBinding.includeRequestexchangeCause.edittextRequestorderstatusCause.addTextChangedListener {
//            mViewModel.mExchangeRequest.exchangeReasonDetail = it.toString()
//        }
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

            // 교환상품 배송지
            mBinding.includeRequestexchangeExchangeshipping.name = it.name
            mBinding.includeRequestexchangeExchangeshipping.address = "[${it.zip}] ${it.roadAddress} ${it.detailAddress}"
            mBinding.includeRequestexchangeExchangeshipping.defaultAddress = it.defaultAddress
        })

        mViewModel.getSellerInfo()
        mViewModel.getSellerDefaultReturnAddress()
    }

    private fun initCollection() {
        mBinding.includeRequestexchangeCollection.title = resources.getString(R.string.requestorderstatus_exchange_way_title)
        mBinding.includeRequestexchangeCollection.description = resources.getString(R.string.requestorderstatus_exchange_way_description)
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
            }
        }

        mViewModel.mShippingCompanyList.observe(this, Observer {
            mBinding.includeRequestexchangeCollection.spinnerRequestorderstatusShippingcompany.adapter = ShippingCompanySpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_payment_spinner, it)
            mBinding.includeRequestexchangeCollection.spinnerRequestorderstatusShippingcompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val company = mViewModel.mShippingCompanyList.value?.get(position)
                    if (company != null) mViewModel.mExchangeRequest.shippingCompanyCode = company.code
                    mBinding.includeRequestexchangeCollection.textviewRequestorderstatusShippingcompany.text = company?.name
                }
            }
            mBinding.includeRequestexchangeCollection.spinnerRequestorderstatusShippingcompany.setSelection(it.size - 1)
        })

        mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.addTextChangedListener {
            mBinding.includeRequestexchangeCollection.textviewRequestorderstatusWarning.visibility = if (it.isNullOrEmpty()) View.VISIBLE
            else View.GONE
        }

        mViewModel.getShippingCompany()
    }

    private fun initShippingPayment() {
        mBinding.includeRequestexchangeShippingpayment.title = resources.getString(R.string.requestorderstatus_exchange_shipping_title)
        mBinding.includeRequestexchangeShippingpayment.textviewRequestorderstatusShippingpaymentDescription1.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_exchange_shipping_description1))
        mBinding.includeRequestexchangeShippingpayment.textviewRequestorderstatusShippingpaymentDescription2.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_exchange_shipping_description2))

        mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.BOX.pos
                mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment3.isChecked = false
            }
        }
        mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mShippingPayment = ShippingPaymentType.DIRECT_SEND.pos
                mBinding.includeRequestexchangeShippingpayment.radiobuttonRequestorderstatusShippingpayment2.isChecked = false
            }
        }
    }

    private fun initExchangeShipping() {
        mBinding.includeRequestexchangeExchangeshipping.title = resources.getString(R.string.requestorderstatus_exchange_address)
        mBinding.includeRequestexchangeExchangeshipping.spinnerRequestorderstatusShippingmemo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val shippingMessage = mViewModel.mShippingMessageList.value?.get(position)
                mBinding.includeRequestexchangeExchangeshipping.textviewRequestorderstatusShippingmemo.text = shippingMessage?.message
                mBinding.includeRequestexchangeExchangeshipping.edittextRequestorderstatusShippingmemo.visibility =
                        if (shippingMessage?.type == ShippingMessageCode.SELF.code) View.VISIBLE
                        else View.GONE
            }
        }

        mViewModel.mShippingMessageList.observe(this, Observer {
            mBinding.includeRequestexchangeExchangeshipping.spinnerRequestorderstatusShippingmemo.adapter = PaymentSpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_payment_spinner, it)
            if (it.isNotEmpty()) mBinding.includeRequestexchangeExchangeshipping.spinnerRequestorderstatusShippingmemo.setSelection(it.size - 1)
        })

        mBinding.includeRequestexchangeExchangeshipping.setChangeShippingListener {
            val intent = Intent(this@RequestExchangeActivity, ShippingAddressActivity::class.java)
            startActivityForResult(intent, RequestCode.SHIPPING_ADDRESS.flag)
        }
        mViewModel.getShippingMessageByOrderForm()
    }

    private fun initButton() {
        mBinding.includeRequestexchangeButton.confirmText = resources.getString(R.string.requestorderstatus_exchange_button_submit)
        mBinding.includeRequestexchangeButton.cancelText = resources.getString(R.string.common_cancel)
        mBinding.includeRequestexchangeButton.setOnClickConfirm {
            val invoiceId =  mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.text.toString()
            if(invoiceId.isNotEmpty())
                mViewModel.mExchangeRequest.invoiceNo = mBinding.includeRequestexchangeCollection.edittextRequestorderstatusShippingid.text.toString().toLong()
            mViewModel.requestExchange()
        }
        mBinding.includeRequestexchangeButton.setOnClickCancel { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.SHIPPING_ADDRESS.flag && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra("shippingAddress").let {
                if (it != null) {
                    val shippingAddress = it as UserShipping
                    mViewModel.mExchangeRequest.exchangeShippingAddress = shippingAddress
                    mBinding.includeRequestexchangeExchangeshipping.address = "[${shippingAddress.zip}] ${shippingAddress.roadAddress} ${shippingAddress.detailAddress}"
                    mBinding.includeRequestexchangeExchangeshipping.defaultAddress = shippingAddress.defaultAddress
                    mBinding.includeRequestexchangeExchangeshipping.name = shippingAddress.shippingName
                }
            }
        }
    }
}