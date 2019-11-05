package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.RefundRequest
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.seller.SellerAddress
import io.temco.guhada.databinding.ActivitySuccessrefundBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 반품 신청 완료
 * @author Hyeyeon Park
 * @since 2019.08.20
 */
class SuccessRequestRefundActivity : BindActivity<ActivitySuccessrefundBinding>() {
    private lateinit var mPurchaseOrder: PurchaseOrder

    override fun getBaseTag(): String = SuccessRequestRefundActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_successrefund

    override fun getViewType(): Type.View = Type.View.SUCCESS_REFUND

    override fun init() {
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null)
                initProductInfo(it as PurchaseOrder)
        }
        intent.getSerializableExtra("sellerAddress").let {
            if (it != null)
                mBinding.sellerAddress = it as SellerAddress
        }
        intent.getSerializableExtra("refundRequest").let {
            if (it != null)
                initRefundInfo(it as RefundRequest)
        }
        mBinding.setOnClickClose { finish() }
        mBinding.executePendingBindings()
    }

    private fun initProductInfo(purchaseOrder: PurchaseOrder) {
        this.mPurchaseOrder = purchaseOrder
        mBinding.includeSuccessrefundProductinfo.imageUrl = purchaseOrder.imageUrl
        mBinding.includeSuccessrefundProductinfo.brandName = purchaseOrder.brandName
        mBinding.includeSuccessrefundProductinfo.productName = "${purchaseOrder.season} ${purchaseOrder.productName}"
        mBinding.includeSuccessrefundProductinfo.price = purchaseOrder.originalPrice
        mBinding.includeSuccessrefundProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText
    }

    private fun initRefundInfo(refundRequest: RefundRequest) {
        this.mPurchaseOrder.quantity = refundRequest.quantity
        mBinding.refundRequest = refundRequest
        mBinding.includeSuccessrefundProductinfo.optionStr = mPurchaseOrder.getOptionStr()
    }
}

