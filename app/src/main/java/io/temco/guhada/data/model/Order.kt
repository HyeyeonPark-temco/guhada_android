package io.temco.guhada.data.model

class Order {
    var user: User = User()
    var shippingAddress: UserShipping = UserShipping()
    var orderItemList: List<OrderItemResponse> = ArrayList()
    var paymentsMethod: List<PaymentMethod> = ArrayList()
    var shippingMessage: MutableList<String> = ArrayList()

    var totalProdPrice = 0
    var totalDiscountDiffPrice = 0
    var totalShipPrice = 0
    var totalPaymentPrice = 0

    class PaymentMethod {
        var methodCode = ""
        var methodName = ""
    }
}