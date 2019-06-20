package io.temco.guhada.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PGAuth : Serializable {
    var acntTxtype = ""
    var authId = ""
    var authToken = ""
    var authUrl = ""
    var billid = ""
    var cardNo = ""
    var cardQuota = ""
    var cashYn = ""
    var cavv = ""
    var checkAckUrl = ""
    var cno = ""
    var eci = ""
    var isPoint = ""
    var kftcSerialno = ""
    var kvpCardcode = ""
    var kvpEncdata = ""
    var kvpNoint = ""
    var kvpPgid = ""
    var kvpQuota = ""
    var kvpSessionKey = ""

    var methodCd = ""
    var mid: Long = 0
    var mobileCd = ""
    var mobileNo = ""
    var netCancel = ""
    var parentMethodCd = ""
    var pgAmount = ""
    var pgKind = ""
    var pgMid = ""
    var pgOid = ""

    var pgTid = ""
    var pgTidSample = ""

    @SerializedName("prodNm")
    var productName = ""

    var productAmt = ""
    var purchaseEmail = ""

    @SerializedName("purchaseNm")
    var purchaseUserName = ""

    var purchasePhone = ""
    var reqType = ""
    var resultCode = ""

    var resultMsg = ""
    var returnUrl = ""
    var trCd = ""
    var vacctExprDate = ""
    var vacctExprTime = ""
    var web = false
    var xid = ""

    // 무통장 입금
    var vbankReceivedCd = ""
    var vbankReceivedNm = ""
    var vbankReceivedNo = ""









}
