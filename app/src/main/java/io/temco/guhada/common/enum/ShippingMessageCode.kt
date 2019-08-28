package io.temco.guhada.common.enum

/**
 * 배송 메세지 code enum 클래스
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
enum class ShippingMessageCode(val code : String, var label:String) {
    BEFORE_CALL("BEFORE_CALL", "배송 전, 연락바랍니다."),
    PUT_DOOR("PUT_DOOR", "문 앞에 놓아주세요."),
    SECURITY("SECURITY","경비실에 맡겨주세요."),
    CALL_IF_ABSENT("CALL_IF_ABSENT","부재시 휴대폰으로 연락바랍니다."),
    POSTBOX("POSTBOX","택배함에 넣어주세요."),
    SELF("SELF","")
}