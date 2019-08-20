package io.temco.guhada.common.enum

/**
 * 교환 신청 사유 enum 클래스
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
enum class ExchangeCause (val code: String, val label: String, val isFeeCharged: Boolean) {
    COLOR_SIZE_CHANGE("COLOR_SIZE_CHANGE", "색상 및 사이즈 변경", true),
    MISTAKE_ORDER("MISTAKE_ORDER", "다른 상품 잘못 주문", true),
    SERVICE_UNSATISFIED("SERVICE_UNSATISFIED", "서비스 불만족", false),
    LATE_DELIVERY("LATE_DELIVERY", "배송지연", false),
    SOLD_OUT("SOLD_OUT", "상품품절", false),
    DELIVERY_OMITTED("DELIVERY_OMITTED", "배송누락", false),
    DAMAGED("DAMAGED", "상품파손", false),
    DIFFERENT_PRODUCT("DIFFERENT_PRODUCT", "상품정보 상이", false),
    MISTAKE_DELIVERY("MISTAKE_DELIVERY", "오배송", false),
    DIFFERENT_OPTION("DIFFERENT_OPTION", "색상등 다른 상품 잘못 배송", false),
    ETC("ETC", "기타", false)
}