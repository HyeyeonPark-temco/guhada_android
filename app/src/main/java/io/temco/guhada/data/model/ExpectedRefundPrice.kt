package io.temco.guhada.data.model

import io.temco.guhada.common.enum.PurchaseStatus
import io.temco.guhada.data.model.order.PurchaseOrder

/**
 * 환불 예상 금액 model
 * @author Hyeyeoon Park
 * @since 2019.09.26
 */
class ExpectedRefundPrice {
    var totalCancelOrderPrice = 0       // "취소상품 주문금액" ///// 총 환불금액도 이걸로 쓰시면됩니다.
    var totalCancelProductPrice = 0     // "취소상품 금액합계"  ///// 취소상품에 그대로 쓰면됩니다.
    var totalCancelShipPrice = 0        // "취소 배송비 합계" ///// 배송비  없으므로 무조건 0 입니다.
    var totalCancelDiscountPrice = 0    // "상품 주문할인 취소" /////  할인부분에 무조건 쓰시면됩니다.
    var totalPaymentCancelPrice = 0     // "신용카드 환불금액" ///// 신용카드가 아니고 결제수단에 맞춰서 텍스트 보여주시면 됩니다!
    var totalPointCancelPrice = 0       // "포인트 환불금액" ///// 포인트에 그대로 쓰시면됩니다.

    class ExpectedRefundInfo : PurchaseOrder(){
        var refundResponse = ExpectedRefundPrice()

        var totalAmount = 0
        var parentMethod = 0

        // purchaseStatus가 WAITING_PAYMENT인 경우(== 무통장 입금인 경우), "환불 정보" 비노출
        fun getIsRefundValid() :Boolean = purchaseStatus == PurchaseStatus.WAITING_PAYMENT.status
    }
}