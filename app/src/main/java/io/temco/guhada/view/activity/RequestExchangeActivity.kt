package io.temco.guhada.view.activity

import android.text.Html
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.OrderChangeCause
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.RequestExchangeViewModel
import io.temco.guhada.databinding.ActivityRequestexchangeBinding
import io.temco.guhada.view.activity.base.BindActivity

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

    }

    private fun initViewModel() {
        mViewModel = RequestExchangeViewModel()
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null) mViewModel.purchaseOrder = it as PurchaseOrder
        }
    }

    private fun initHeader() {
        mBinding.includeRequestexchangeHeader.title = resources.getString(R.string.requestorderstatus_exchange_title)
        mBinding.includeRequestexchangeHeader.setOnClickBackButton { finish() }
    }

    private fun initOrderInfo() {
        mBinding.includeRequestexchangeOrderinfo.orderNumber = mViewModel.purchaseOrder.purchaseId.toInt()
        mBinding.includeRequestexchangeOrderinfo.orderTimeStamp = mViewModel.purchaseOrder.orderTimestamp
    }

    private fun initProductInfo() {
        mBinding.includeRequestexchangeProductinfo.imageUrl = mViewModel.purchaseOrder.imageUrl
        mBinding.includeRequestexchangeProductinfo.brandName = mViewModel.purchaseOrder.brandName
        mBinding.includeRequestexchangeProductinfo.productName = "${mViewModel.purchaseOrder.season} ${mViewModel.purchaseOrder.dealName}"
        mBinding.includeRequestexchangeProductinfo.optionStr = mViewModel.purchaseOrder.getOptionStr()
        mBinding.includeRequestexchangeProductinfo.price = mViewModel.purchaseOrder.discountPrice
        mBinding.includeRequestexchangeProductinfo.purchaseStatusText = mViewModel.purchaseOrder.purchaseStatusText
    }

    private fun initCause() {
        val causeList = mutableListOf(
                OrderChangeCause().apply {
                    label = "교환사유1 (배송비 X)"
                    isFeeCharged = false
                },
                OrderChangeCause().apply {
                    label = "교환사유2 (배송비 O)"
                    isFeeCharged = true
                })

        mBinding.includeRequestexchangeCause.defaultMessage = resources.getString(R.string.requestorderstatus_exchange_cause)
        mBinding.includeRequestexchangeCause.hintMessage = resources.getString(R.string.requestorderstatus_exchange_hint_cause)
        mBinding.includeRequestexchangeCause.quantityTitle = resources.getString(R.string.requestorderstatus_exchange_quantity)
        mBinding.includeRequestexchangeCause.quantity = 1
        mBinding.includeRequestexchangeCause.sellerName = "셀러명"
        mBinding.includeRequestexchangeCause.setOnClickAmountMinus {
            val quantity = mBinding.includeRequestexchangeCause.quantity
            if (quantity ?: 0 - 1 <= 0) ToastUtil.showMessage("교환 가능 최소 수량 0개")
        }
        mBinding.includeRequestexchangeCause.setOnClickAmountPlus {
            val quantity = mBinding.includeRequestexchangeCause.quantity
            if (quantity ?: 0 + 1 > mViewModel.purchaseOrder.quantity) ToastUtil.showMessage("교환 가능 최대 수량 ${mViewModel.purchaseOrder.quantity}개")
        }
        mBinding.includeRequestexchangeCause.causeList = causeList
        mBinding.includeRequestexchangeCause.spinnerRequestorderstatusCause.setOnItemClickListener { parent, view, position, id ->
            mBinding.includeRequestexchangeCause.defaultMessage = causeList[position].label
            mBinding.includeRequestexchangeShippingpayment.framelayoutRequestorderstatusShippingpaymentContainer.visibility =
                    if (causeList[position].isFeeCharged) View.GONE
                    else View.VISIBLE
            mBinding.executePendingBindings()
        }
    }

    private fun initSellerShipping() {
        mBinding.includeRequestexchangeSellershipping.title = resources.getString(R.string.requestorderstatus_exchange_selleraddress)
        mBinding.includeRequestexchangeSellershipping.address = "셀러 주소"
        mBinding.includeRequestexchangeSellershipping.sellerName = mViewModel.purchaseOrder.sellerName
        mBinding.includeRequestexchangeSellershipping.sellerPhone = "셀러 핸드폰 번호"
    }

    private fun initCollection() {
        mBinding.includeRequestexchangeCollection.title = resources.getString(R.string.requestorderstatus_exchange_way_title)
        mBinding.includeRequestexchangeCollection.description = resources.getString(R.string.requestorderstatus_exchange_way_description)
    }

    private fun initShippingPayment() {
        mBinding.includeRequestexchangeShippingpayment.title = resources.getString(R.string.requestorderstatus_exchange_shipping_title)
        mBinding.includeRequestexchangeShippingpayment.textviewRequestorderstatusShippingpaymentDescription1.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_exchange_shipping_description1))
        mBinding.includeRequestexchangeShippingpayment.textviewRequestorderstatusShippingpaymentDescription2.text = Html.fromHtml(resources.getString(R.string.requestorderstatus_exchange_shipping_description2))
    }

    private fun initExchangeShipping() {
        mBinding.includeRequestexchangeExchangeshipping.title = resources.getString(R.string.requestorderstatus_exchange_address)
        //mBinding.includeRequestexchangeExchangeshipping.defaultAddress = "기본 배송지 주소"
        //mBinding.includeRequestexchangeExchangeshipping.defaultName = "기본 배송지 명"
    }

    private fun initButton() {
        mBinding.includeRequestexchangeButton.confirmText = resources.getString(R.string.requestorderstatus_exchange_button_submit)
        mBinding.includeRequestexchangeButton.cancelText = resources.getString(R.string.common_cancel)
        mBinding.includeRequestexchangeButton.setOnClickConfirm { }
        mBinding.includeRequestexchangeButton.setOnClickCancel { finish() }
    }

}