package io.temco.guhada.data.model

import java.io.Serializable

class Payment: Serializable {
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
    var vbankExpireAt = ""
    var vbankNo = ""
    var vbankRemitterName = ""
}