package io.temco.guhada.data.model

class RequestOrder {
    var user: User = User()
    var addShippingAddress = false
    var cartItemIdList: Array<Long> = arrayOf()
    var parentMethodCd: String = ""
    var shippingAddress: UserShipping = UserShipping()

    var recepientMobile = ""
    var recepientName = ""

//    var couponPayments
//    var pointPayments

    // WEB 관련
    var userAgent: String = ""
    var web: Boolean = false

}