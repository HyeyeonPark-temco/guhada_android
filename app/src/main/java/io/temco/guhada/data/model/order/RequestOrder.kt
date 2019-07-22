package io.temco.guhada.data.model.order

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.User
import io.temco.guhada.data.model.UserShipping

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