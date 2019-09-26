package io.temco.guhada.data.model.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.OrderChangeCause
import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import java.io.Serializable

/**
 * 주문 정보 클래스
 * @see PurchaseOrderResponse
 * @author Hyeyeon Park
 */
class PurchaseOrder : OrderItemResponse(), Serializable {
    var purchaseId: Long = 0
    var expireTimestamp: Long = 0
    var orderTimestamp: Long = 0

    var optionAttribute1: String = ""
    var optionAttribute2: String = ""
    var optionAttribute3: String = ""

    var orderPrice = 0
    var orderProdGroupId: Long = 0
    var orderProdId: Long = 0
    var originalPrice = 0

    @SerializedName("prodName")
    var productName = ""

    var purchaseConfirm = false // 구매 확정 여부
    var purchaseStatus = ""
    var purchaseStatusText = ""

    var shipCompleteDate = "" // 2019-05-10
    var shipCompleteTimestamp: String = ""
    var shipPrice = 0
    var statusMessage = "" // 주문 완료 메세지
    var reviewId: Int? = null // 리뷰 작성 여부 판단

    // 취소교환반품 사유
    var cancelReasonList: MutableList<OrderChangeCause>? = mutableListOf()
    var exchangeReasonList: MutableList<OrderChangeCause>? = mutableListOf()
    var returnReasonList: MutableList<OrderChangeCause>? = mutableListOf()

    // 완료 화면 정보
    var orderStatus = ""
    var claimStatus = ""
    var claimStatusText = ""
    var paymentMethodText = ""
    var orderStatusText = ""

    // 신청서 수정 정보
    var orderClaimId: Long = 0L
    var orderClaimGroupId: Long = 0L
    var receiverAddress = ""
    var receiverAddressDetail = ""
    var receiverAddressName = ""
    var receiverName = ""
    var receiverPhone = ""
    var receiverRoadAddress = ""
    var receiverZipcode = ""
    var receiverMessage = ""
    var paymentMethod = ""
    var pgTid = ""
    var buyerEmail = ""
    var buyerName = ""
    var buyerPhone = ""

    // 취소 신청 정보
    var cancelReason: String = ""
    var cancelReasonDetail = ""

    // 반품 신청 정보
    var returnReason = ""
    var returnReasonDetail = ""
    var returnPickingShipCompany = ""  // [19.08.26] 택배사 코드
    var returnPickingInvoiceNo = ""
    var returnShippingPriceType = ""
    var returnShippingPrice = 0
    var returnEtcPrice = 0

    // 교환 신청 정보
    var exchangeReason = ""
    var exchangeReasonDetail = ""
    var exchangePickingShipCompany = ""
    var exchangePickingInvoiceNo = ""
    var exchangeShippingPriceType = ""
    var exchangeShippingPrice = 0
    var exchangeEtcPrice = 0
    var exchangeBuyerRecipientName = ""
    var exchangeBuyerRecipientMobile = ""
    var exchangeBuyerZip = ""
    var exchangeBuyerAddress = ""
    var exchangeBuyerRoadAddress = ""
    var exchangeBuyerDetailAddress = ""
    var exchangeBuyerShippingMessage = ""

    var shippingMessageList = mutableListOf<ShippingMessage>()

    // 반품 신청 환불 계좌 은행
    var banks = mutableListOf<Bank>()

    fun getOptionStr(): String {
        var result = ""
        if (!optionAttribute1.isNullOrEmpty()) result += optionAttribute1
        if (!optionAttribute2.isNullOrEmpty()) result += ", $optionAttribute2"
        if (!optionAttribute3.isNullOrEmpty()) result += ", $optionAttribute3"

        result = if (result.isEmpty()) "${quantity}개"
        else "$result, ${quantity}개"

        return result
    }

    fun getDate() = CommonUtil.convertTimeStampToDate(orderTimestamp) ?: ""
    fun getStatus(): String = if (orderStatus.isEmpty()) claimStatus else orderStatus

    class Bank : Serializable {
        var bankCode = ""       // ex: "81"
        var bankName = ""       // ex: "하나은행"
        var bankPriority = ""   // ex: "10"
    }

}
