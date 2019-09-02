package io.temco.guhada.data.model

import com.google.gson.annotations.SerializedName
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ShippingPaymentType
import java.io.Serializable

/**
 * 주문 반품 정보
 * @see ShippingPaymentType
 * @author Hyeyeon Park
 */
class RefundRequest : Serializable {
    var alreadySend  = false
    var claimShippingPriceType = ""
    var invoiceNo = 0L
    var orderProdGroupId = 0L
    var quantity = 1
    var shippingCompanyCode = ""
    var shippingCompanyName = ""
    var orderClaimId = 0L

    @SerializedName("returnReasonDetail")
    var refundReasonDetail = ""

    @SerializedName("returnReason")
    var refundReason = ""

    fun getShippingPaymentDescription(): String {
        val context = BaseApplication.getInstance()
        return when (claimShippingPriceType) {
            ShippingPaymentType.NONE.type -> "판매자 부담"
            ShippingPaymentType.BOX.type -> String.format(context.getString(R.string.successrefund_shippingpayment), context.getString(R.string.requestorderstatus_common_shipping_radio1))
            ShippingPaymentType.DIRECT_SEND.type -> String.format(context.getString(R.string.successrefund_shippingpayment), context.getString(R.string.requestorderstatus_common_shipping_radio2))
            else -> "판매자 부담"
        }
    }
}
