package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.ShippingMessage
import io.temco.guhada.data.model.User
import io.temco.guhada.data.model.UserShipping

class Order {
    var user: User = User()
    var shippingAddress: UserShipping? = UserShipping()
    var orderItemList: List<OrderItemResponse> = ArrayList()
    var paymentsMethod: MutableList<PaymentMethod> = ArrayList()
    var shippingMessage: MutableList<ShippingMessage> = ArrayList()

//    @Expose
//    var shippingMessageType : MutableList<ShippingMessage> = ArrayList()


    var totalProdPrice = 0
    var totalDiscountDiffPrice = 0
    var totalShipPrice = 0
    var totalPaymentPrice = 0

    class PaymentMethod {
        var methodCode = ""
        var methodName = ""
    }
}