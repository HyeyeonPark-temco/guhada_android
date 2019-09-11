package io.temco.guhada.data.model.order

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.model.UserShipping

/**
 * 결제창(PG사 화면)을 띄우기위해 전달되는 클래스
 * @author Hyeyeon Park
 */
class RequestOrder {
    /**
     * 주문 User 정보 deprecated 처리
     * @since 2019.07.30
     * @author Hyeyeon Park
     */
    var user: User = User()
    var addShippingAddress = false
    // var cartItemIdList: Array<Long?> = arrayOf()
    var cartItemPayments = mutableListOf<CartItemPayment>()
    var parentMethodCd: String = ""
    var shippingAddress: UserShipping = UserShipping()

    @SerializedName(value = "recipientMobile", alternate = ["recepientMobile"])
    var recipientMobile = ""

    @SerializedName(value = "recipientName", alternate = ["recepientName"])
    var recipientName = ""


    /** 현금영수증 요청번호(전화번호, 사업자등록번호, 현금영수증 카드번호...etc) */
    var cashReceiptNo = ""

    /**
     * 현금영수증 용도(PERSONAL:개인소득공제용, BUSINESS:사업자증빙용)
     * @see CashReceiptUsage
     */
    var cashReceiptUsage = ""

    /**
     * 현금영수증 요청방식 (MOBILE:휴대폰번호, CARD:현금영수증카드, BUSINESS:사업자번호)
     * @see CashReceiptType
     */
    var cashReceiptType = ""

    // WEB 관련
    var userAgent: String = ""
    var web: Boolean = false

    class CartItemPayment {
        var cartItemId = 0L
        var couponNumber = ""
    }

    enum class CashReceiptUsage(val usage: String) {
        PERSONAL("PERSONAL"),
        BUSINESS("BUSINESS")
    }

    enum class CashReceiptType(val type: String) {
        MOBILE("MOBILE"),
        CARD("CARD"),
        BUSINESS("BUSINESS")
    }
}