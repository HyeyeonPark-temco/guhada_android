package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
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
        mPurchaseOrder = purchaseOrder
        mBinding.includeSuccessexchangeProductinfo.imageUrl = purchaseOrder.imageUrl
        mBinding.includeSuccessexchangeProductinfo.brandName = purchaseOrder.brandName
        mBinding.includeSuccessexchangeProductinfo.productName = "${purchaseOrder.season} ${purchaseOrder.productName}"
        mBinding.includeSuccessexchangeProductinfo.price = purchaseOrder.originalPrice
        mBinding.includeSuccessexchangeProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText
    }

    private fun initExchangeInfo(exchangeRequest: ExchangeRequest){
        mPurchaseOrder.quantity = exchangeRequest.quantity
        mOption = mPurchaseOrder.getOptionStr()
        mBinding.exchangeRequest = exchangeRequest
        mBinding.includeSuccessexchangeProductinfo.optionStr = mOption
    }
}

