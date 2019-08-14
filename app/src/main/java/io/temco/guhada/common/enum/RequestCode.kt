package io.temco.guhada.common.enum

/**
 * RequestCode enum clsss
 * 기존 Flag.class에서 RequestCode를 분리
 * @see io.temco.guhada.common.Flag
 * @author Hyeyeon Park
 */
enum class RequestCode(val flag: Int) {
    VERIFY_PHONE(10001),
    KAKAO_LOGIN(10002),
    NAVER_LOGIN(10003),
    FACEBOOK_LOGIN(10004),
    GOOGLE_LOGIN(10005),
    RC_GOOGLE_LOGIN(10006),
    WRITE_CLAIM(10007),
    LOGIN(101),
    PAYMENT(10009),
    PAYMENT_WEBVIEW(10010),
    SHIPPING_ADDRESS(10011),
    EDIT_SHIPPING_ADDRESS(10012),
    ADD_SHIPPING_ADDRESS(10013),
    SEARCH_ZIP(10014),
    MODIFY_CLAIM(10015),
    PRODUCT_DETAIL(10016),
    SIDE_MENU(10018),
    REVIEW_WRITE(10020),
    REVIEW_MODIFY(10021)
}