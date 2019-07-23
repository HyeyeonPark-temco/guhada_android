package io.temco.guhada.common.flag

/**
 * ResultCode enum clsss
 * 기존 Flag.class에서 ResultCode를 분리
 * @see io.temco.guhada.common.Flag
 * @author Hyeyeon Park
 */
enum class ResultCode(val flag: Int) {
    SUCCESS(200),
    ORDER_NOT_VALID_ERROR(1001),
    DATA_NOT_FOUND(5004),
    NOT_FOUND_VERIFY_INFO(5400),
    ALREADY_EXIST_EMAIL(6001),
    SIGNUP_INVALID_PASSWORD(6002),
    SIGNIN_INVALID_PASSWORD(6003),
    EXPIRED_VERIFICATION_NUMBER(6004),
    ALREADY_SIGNED_UP(6006),
    INVALID_VERIFICATION_NUMBER(6007),
    USER_NOT_FOUND(6016),
    NEED_TO_LOGIN(6017),
    PRODUCT_RESOURCE_NOT_FOUND(8404)
}