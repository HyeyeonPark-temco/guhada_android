package io.temco.guhada.common.enum

/**
 * 결제 수단 enum 클래스
 * @author Hyeyeon Park
 * @since 2019.09.09
 */
enum class PaymentWayType(val code: String, val label: String) {
    CARD("Card", "신용/체크카드"),
    VBANK("VBank", "무통장입금"),
    DIRECT_BANK("DirectBank", "실시간 계좌이체"),
    TOKEN("TOKEN", "토큰결제")
}