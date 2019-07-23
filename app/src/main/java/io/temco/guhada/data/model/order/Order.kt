package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.model.UserShipping

/**
 * 주문 정보 클래스
 * @author Hyeyeon Park
 */
class Order {
    var user: User = User()
    var shippingAddress: UserShipping? = UserShipping()
    var orderItemList: List<OrderItemResponse> = ArrayList()
    var paymentsMethod: MutableList<PaymentMethod> = ArrayList()
    var shippingMessage: MutableList<ShippingMessage> = ArrayList()

    // PRICE
    var totalProdPrice = 0
    var totalDiscountDiffPrice = 0
    var totalShipPrice = 0
    var totalPaymentPrice = 0
}