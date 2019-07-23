package io.temco.guhada.data.model.order

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.model.UserShipping

/**
 * 결제창(PG사 화면)을 띄우기위해 전달되는 클래스
 * @author Hyeyeon Park
 */
class RequestOrder {
    var user: User = User()
    var addShippingAddress = false
    var cartItemIdList: Array<Long?> = arrayOf()
    var parentMethodCd: String = ""
    var shippingAddress: UserShipping = UserShipping()

    @SerializedName(value="recipientMobile", alternate = ["recepientMobile"])
    var recipientMobile = ""

    @SerializedName(value="recipientName", alternate = ["recepientName"])
    var recipientName = ""

//    var couponPayments
//    var pointPayments

    // WEB 관련
    var userAgent: String = ""
    var web: Boolean = false

}