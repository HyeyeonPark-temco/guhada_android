package io.temco.guhada.common.enum

/**
 * 쿠폰, 포인트 saveActionType enum class
 * @author Hyeyeon Park
 * @since 2019.09.10
 */
enum class SaveActionType(val type: String) {
    BUY("BUY"),
    FOLLOW("FOLLOW"),
    BEST_REVIEW("BEST_REVIEW"),
    USER_REGISTRATION("USER_REGISTRATION")
}