package io.temco.guhada.data.model.payment

import com.google.gson.annotations.Expose
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.order.RequestOrder.CashReceiptType
import io.temco.guhada.data.model.order.RequestOrder.CashReceiptUsage
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
    var pointPayment = 0

    var completeAt = intArrayOf()
    var completeTimestamp = 0L
    var mobileCorp = ""
    var mobileVanCd = ""
    var paymentStatus = ""
    var requestAt = intArrayOf()
    var requestTimestamp = 0L
    var vbankBankName = ""
    var vbankDepositorName = ""
    var vbankExpireAt = intArrayOf()
    var vbankExpireTimestamp = 0L
    var vbankNo = ""
    var vbankRemitterName = ""

    /** 현금영수증 요청번호(전화번호, 사업자등록번호, 현금영수증 카드번호...etc) */
    var cashReceiptNo = ""

    /**
     * 현금영수증 용도(PERSONAL:개인소득공제용, BUSINESS:사업자증빙용)
     * @see CashReceiptUsage
     */
    var cashReceiptUsage = ""

    /**
     * 현금영수증 요청방식 (MOBILE:휴대폰번호, CARD:현금영수증카드, BUSINESS:사업자번호)
     * @see CashReceiptType
     */
    var cashReceiptType = ""


    @Expose
    var methodName = ""

    @Expose
    var completeAtText = ""

    fun getVBankExpiredAt(): String = "${CommonUtil.convertTimeStampToDate(vbankExpireTimestamp)} 까지"


}