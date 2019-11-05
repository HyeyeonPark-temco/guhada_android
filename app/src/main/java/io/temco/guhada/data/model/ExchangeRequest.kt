package io.temco.guhada.data.model

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ShippingPaymentType
import java.io.Serializable

/**
 * 교환 신청 클래스
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
class ExchangeRequest : Serializable {
    var alreadySend = true
    var claimShippingPriceType = ""
    var exchangeReason = ""
    var exchangeReasonDetail = ""
    var shippingCompanyCode = ""
    var shippingCompanyName = ""
    var invoiceNo = 0L
    var orderProdGroupId = 0L
    var quantity = 1
    var orderClaimId = 0L
    var exchangeShippingAddress: UserShipping = UserShipping()

    fun getShippingPaymentDescription(): String {
        val context = BaseApplication.getInstance()
        return when (claimShippingPriceType) {
            ShippingPaymentType.NONE.type -> "판매자 부담"
            ShippingPaymentType.BOX.type -> String.format(context.getString(R.string.successexchange_shippingpayment), context.getString(R.string.requestorderstatus_common_shipping_radio1))
            ShippingPaymentType.DIRECT_SEND.type -> String.format(context.getString(R.string.successexchange_shippingpayment), context.getString(R.string.requestorderstatus_common_shipping_radio2))
            else -> "판매자 부담"
        }
    }

    fun getInvoiceInfo(): String {
        return if (invoiceNo > 0) "$shippingCompanyName $invoiceNo"
        else shippingCompanyName
    }
}
