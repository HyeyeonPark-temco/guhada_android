package io.temco.guhada.view.activity

import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.PaymentWayType
import io.temco.guhada.common.enum.PurchaseStatus
import io.temco.guhada.common.enum.ShippingPaymentType
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
    private var mOption = ""
    private var returnShippingPrice = 0

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
        mBinding.purchaseOrder = purchaseOrder
        this.mPurchaseOrder = purchaseOrder
        mBinding.includeSuccessrefundProductinfo.imageUrl = purchaseOrder.imageUrl
        mBinding.includeSuccessrefundProductinfo.brandName = purchaseOrder.brandName
        mBinding.includeSuccessrefundProductinfo.productName = "${purchaseOrder.season} ${purchaseOrder.productName}"
        mBinding.includeSuccessrefundProductinfo.price = purchaseOrder.originalPrice
        mBinding.includeSuccessrefundProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText

        returnShippingPrice = if (mPurchaseOrder.returnShippingPrice > 0) mPurchaseOrder.returnShippingPrice else mPurchaseOrder.returnShipExpense
        val shippingPriceVisibility = if (purchaseOrder.returnShippingPrice > 0 || purchaseOrder.returnShipExpense > 0) View.VISIBLE else View.GONE
        mBinding.textviewSuccessrefundShippingprice.visibility = shippingPriceVisibility
        mBinding.textviewSuccessrefundShippingtitle.visibility = shippingPriceVisibility

        val shippingPriceLineVisibility = if (purchaseOrder.paymentMethod == PaymentWayType.VBANK.code && shippingPriceVisibility == View.VISIBLE) View.VISIBLE else View.GONE
        mBinding.viewSuccessrefundShippingline.visibility = shippingPriceLineVisibility
//        mBinding.viewSuccessrefundCollectline.visibility = shippingPriceLineVisibility
    }

    private fun initRefundInfo(refundRequest: RefundRequest) {
        mBinding.refundRequest = refundRequest
        mPurchaseOrder.quantity = refundRequest.quantity
        mOption = mPurchaseOrder.getOptionStr()
        mBinding.includeSuccessrefundProductinfo.optionStr = mOption

        if (refundRequest.claimShippingPriceType != ShippingPaymentType.NONE.type) {
            mBinding.textviewSuccessrefundShippingprice.text = refundRequest.getShippingPaymentDescription(returnShippingPrice)
        } else {
            mBinding.textviewSuccessrefundShippingprice.visibility = View.GONE
            mBinding.textviewSuccessrefundShippingtitle.visibility = View.GONE
        }

        mBinding.textviewSuccessrefundShippingprice.text = refundRequest.getShippingPaymentDescription(returnShippingPrice)
        val refundInfoVisibility = if (mPurchaseOrder.paymentMethod == PaymentWayType.VBANK.code && mPurchaseOrder.orderStatus != PurchaseStatus.WAITING_PAYMENT.status) View.VISIBLE else View.GONE
        mBinding.textviewSuccessrefundRefundaccounttitle.visibility = refundInfoVisibility
        mBinding.textviewSuccessrefundRefundaccount.visibility = refundInfoVisibility
    }
}

