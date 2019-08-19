package io.temco.guhada.common.enum

/**
 * 배송비 지불 방식 enum 클래스
 * [NONE] 판매자 귀책 사유가 아닐 때는 배송비 없음
 * [BOX] 박스에 동봉
 * [DIRECT_SEND] 판매자에게 직접 송금
 *
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
enum class ShippingPaymentType(val type: String) {
    NONE("NONE"),
    BOX("BOX"),
    DIRECT_SEND("DIRECT_SEND")
}