package io.temco.guhada.data.model.payment

import java.io.Serializable

/**
 * 주문서-결제 금액 정보
 * @author Hyeyeon Park
 * @since 2019.10.01
 */
class CalculatePaymentInfo : Serializable {
    var discountInfoResponseList = mutableListOf<DiscountInfoResponse>()         // 상품 할인 금액 목록 (상품 할인, 쿠폰 할인, ...)
    var totalDueSavePointResponseList = mutableListOf<DueSavePointResponse>()   // 적립 예정 포인트
    var totalDiscountPointPrice = 0     // 할인금액(전체 할인금액 : 상품할인 + 쿠폰할인 + 포인트)
    var totalDiscountPrice = 0          // 할인금액(전체 할인금액 : 상품할인 + 쿠폰할인)
    var totalDueSavePoint = 0           // 적립 예정 포인트
    var totalPaymentPrice = 0           // 최종 결제 금액
    var totalPointPrice = 0             // 포인트 사용 금액
    var totalProductPrice = 0           // 상품금액
    var totalShipPrice = 0              // 배송비 금액

    class DiscountInfoResponse : Serializable {
        var discountPrice = 0   // 할인 금액
        var discountType = ""   // 할인 내역

        enum class DiscountType(val type: String, val label: String) {
            PRODUCT_DISCOUNT("PRODUCT_DISCOUNT", "상품 할인"),
            COUPON_DISCOUNT("COUPON_DISCOUNT", "쿠폰 할인")
        }
    }

    class DueSavePointResponse : Serializable {
        var totalPoint = 0
        var dueSaveType = ""
    }

}