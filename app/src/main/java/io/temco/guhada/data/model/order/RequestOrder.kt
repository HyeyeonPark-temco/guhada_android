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

    // WEB 관련
    var userAgent: String = ""
    var web: Boolean = false

    class CartItemPayment {
        var cartItemId = 0L
        var couponNumber = ""
    }
}