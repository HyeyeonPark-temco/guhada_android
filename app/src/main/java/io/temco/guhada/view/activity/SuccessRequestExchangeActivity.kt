package io.temco.guhada.view.activity

import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.PaymentWayType
import io.temco.guhada.common.enum.PurchaseStatus
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.data.model.ExchangeRequest
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.seller.SellerAddress
import io.temco.guhada.databinding.ActivitySuccessexchangeBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 교환 신청 완료
 * @author Hyeyeon Park
 * @since 2019.08.17
 */
class SuccessRequestExchangeActivity : BindActivity<ActivitySuccessexchangeBinding>() {
    private lateinit var mPurchaseOrder: PurchaseOrder
    private var mOption = ""
    private var exchangeShippingPrice = 0

    override fun getBaseTag(): String = SuccessRequestExchangeActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_successexchange

    override fun getViewType(): Type.View = Type.View.SUCCESS_EXCHANGE

    override fun init() {
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null) initProductInfo(it as PurchaseOrder)
        }
        intent.getSerializableExtra("sellerAddress").let {
            if (it != null) mBinding.sellerAddress = it as SellerAddress
        }
        intent.getSerializableExtra("exchangeRequest").let {
            if (it != null) initExchangeInfo(it as ExchangeRequest)
        }
        mBinding.setOnClickClose { finish() }
        mBinding.executePendingBindings()
    }

    private fun initProductInfo(purchaseOrder: PurchaseOrder) {
        mBinding.purchaseOrder = purchaseOrder
        mPurchaseOrder = purchaseOrder
        mBinding.includeSuccessexchangeProductinfo.imageUrl = purchaseOrder.imageUrl
        mBinding.includeSuccessexchangeProductinfo.brandName = purchaseOrder.brandName
        mBinding.includeSuccessexchangeProductinfo.productName = "${purchaseOrder.season} ${purchaseOrder.productName}"
        mBinding.includeSuccessexchangeProductinfo.price = purchaseOrder.originalPrice
        mBinding.includeSuccessexchangeProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText

        exchangeShippingPrice = if (mPurchaseOrder.exchangeShippingPrice > 0) mPurchaseOrder.exchangeShippingPrice else mPurchaseOrder.exchangeShipExpense
        val shippingPriceVisibility = if (purchaseOrder.exchangeShippingPrice > 0 || purchaseOrder.exchangeShipExpense > 0) View.VISIBLE else View.GONE
        mBinding.textviewSuccessexchangeShippingprice.visibility = shippingPriceVisibility
        mBinding.textviewSuccessexchangeShippingtitle.visibility = shippingPriceVisibility

        val shippingPriceLineVisibility = if (purchaseOrder.paymentMethod == PaymentWayType.VBANK.code && shippingPriceVisibility == View.VISIBLE) View.VISIBLE else View.GONE
        mBinding.viewSuccessexchangeShippingline.visibility = shippingPriceLineVisibility
    }

    private fun initExchangeInfo(exchangeRequest: ExchangeRequest) {
        mBinding.exchangeRequest = exchangeRequest
        mPurchaseOrder.quantity = exchangeRequest.quantity
        mOption = mPurchaseOrder.getOptionStr()
        mBinding.includeSuccessexchangeProductinfo.optionStr = mOption

        if (exchangeRequest.claimShippingPriceType != ShippingPaymentType.NONE.type) {
            mBinding.textviewSuccessexchangeShippingprice.text = exchangeRequest.getShippingPaymentDescription(exchangeShippingPrice)
        } else {
            mBinding.textviewSuccessexchangeShippingprice.visibility = View.GONE
            mBinding.textviewSuccessexchangeShippingtitle.visibility = View.GONE
        }
    }
}

