package io.temco.guhada.common.enum

/**
 * 주문 취소 신청 사유 enum 클래스
 * @author Hyeyeon Park
 * @since 2019.08.07
 */
enum class CancelOrderCause(val code: String, val label: String, val userFault: Boolean) {
    CHANGE_MIND("CHANGE_MIND", "구매의사 취소", true),
    COLOR_SIZE_CHANGE("COLOR_SIZE_CHANGE", "색상 및 사이즈 변경", true),
    MISTAKE_ORDER("MISTAKE_ORDER", "다른 상품 잘못 주문", true),
    LATE_DELIVERY("LATE_DELIVERY", "배송지연", false),
    SOLD_OUT("SOLD_OUT", "상품품절", false),
    SERVICE_UNSATISFIED("SERVICE_UNSATISFIED", "서비스 불만족", false),
    DIFFERENT_PRODUCT("DIFFERENT_PRODUCT", "상품정보 상이", false)
}