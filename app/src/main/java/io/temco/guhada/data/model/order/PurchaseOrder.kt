package io.temco.guhada.data.model.order

import com.google.gson.annotations.SerializedName
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.OrderChangeCause
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

    var optionAttribute1: String? = ""
    var optionAttribute2: String? = ""
    var optionAttribute3: String? = ""

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
    var shipCompleteTimestamp: String? = ""
    var shipPrice = 0

    var statusMessage = "" // 주문 완료 메세지

    var reviewId: Int? = null // 리뷰 작성 여부 판단


    // 취소교환반품 사유
    var cancelReasonList: MutableList<OrderChangeCause>? = mutableListOf()
    var exchangeReasonList: MutableList<OrderChangeCause>? = mutableListOf()
    var returnReasonList: MutableList<OrderChangeCause>? = mutableListOf()

    // 완료 화면 정보
    var cancelReason = ""
    var cancelReasonDetail = ""
    var orderStatus = ""
    var claimStatus = ""
    var claimStatusText = ""
    var paymentMethodText = ""
    var orderStatusText = ""

    fun getOptionStr(): String {
        var result = ""
        if (optionAttribute1 != null) {
            result = "$result$optionAttribute1"
        }

        if (optionAttribute2 != null) {
            result = ", $result$optionAttribute2"
        }

        if (optionAttribute3 != null) {
            result = ", $result$optionAttribute3"
        }

        result = if (result.isEmpty()) "${quantity}개"
        else "$result, ${quantity}개"

        return result
    }

    fun getDate() = CommonUtil.convertTimeStampToDate(orderTimestamp) ?: ""
    fun getStatus() : String = if(orderStatus.isEmpty()) claimStatus else orderStatus

}
