package io.temco.guhada.data.model.payment

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PGResponse : Serializable {
    @SerializedName("purchaseNm")
    var purchaseUserName = ""

    @SerializedName("prodNm")
    var productName = ""

    @SerializedName("firmNm")
    var firmName = ""

    @SerializedName("mallNm")
    var mallName = ""

    var acceptMethod = ""
    var appScheme = ""
    var cardCd = ""
    var cardQuota = ""
    var chainCode = ""
    var charset = ""
    var currency = ""
    var epOption = ""
    var epType = ""
    var errorCode = ""
    var expireDate = ""
    var inputOption = ""
    var jsUrl = ""
    var key = ""
    var kmotionUseyn = ""
    var kvpPgid = ""
    var methodCd = ""
    var mobileCd = ""
    var msgCode = ""
    var msgType = ""
    var nextUrl = ""
    var offerPeriod = ""
    var parentMethodCd = ""
    var payViewType = ""
    var payshotKey = ""
    var pgAmount = ""
    var pgKind = ""
    var pgMid = ""
    var pgOid = ""
    var preMsgType = ""
    var purchaseEmail = ""
    var purchasePhone = ""
    var returnUrl = ""
    var serialNo = ""
    var signature = ""
    var timeOutYn = ""
    var timeout = ""
    var timestamp = ""
    var version = ""
}


