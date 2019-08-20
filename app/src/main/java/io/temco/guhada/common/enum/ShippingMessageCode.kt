package io.temco.guhada.common.enum

/**
 * 배송 메세지 code enum 클래스
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
enum class ShippingMessageCode(val code : String) {
    BEFORE_CALL("BEFORE_CALL"),
    PUT_DOOR("PUT_DOOR"),
    SECURITY("SECURITY"),
    CALL_IF_ABSENT("CALL_IF_ABSENT"),
    POSTBOX("POSTBOX"),
    SELF("SELF")
}