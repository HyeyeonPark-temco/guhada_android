package io.temco.guhada.data.model.payment

import com.google.gson.annotations.Expose
import java.io.Serializable

/**
 * @see io.temco.guhada.data.model.order.PurchaseOrderResponse
 * @author Hyeyeon Park
 */
class Payment : Serializable {
    var amount = 0
    var cardQuota = ""
    var method = ""
    var mobileNo = ""
    var parentMethod = ""
    var cashReceiptNo: Long = 0
    var cashReceiptType = ""
    var cashReceiptUsage = ""
    var completeAt = intArrayOf()
    var mobileCorp = ""
    var mobileVanCd = ""
    var paymentStatus = ""
    var requestAt = intArrayOf()
    var vbankBankName = ""
    var vbankDepositorName = ""
    var vbankExpireAt = intArrayOf()
    var vbankNo = ""
    var vbankRemitterName = ""

    @Expose
    var methodName = ""

    @Expose
    var completeAtText = ""

}