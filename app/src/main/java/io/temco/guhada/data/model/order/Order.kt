package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.coupon.AvailableCouponWallet
import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.data.model.user.User
import java.io.Serializable

/**
 * 주문 정보 클래스
 * @author Hyeyeon Park
 */
class Order : Serializable {
    var user: User = User()
    var shippingAddress: UserShipping? = UserShipping()
    var orderItemList: List<OrderItemResponse> = ArrayList()
    var paymentsMethod: MutableList<PaymentMethod> = ArrayList()
    var shippingMessage: MutableList<ShippingMessage> = ArrayList()
    var availablePointResponse = AvailablePoint()

    // PRICE
    var totalProdPrice = 0
    var totalDiscountDiffPrice = 0
    var totalShipPrice = 0
    var totalPaymentPrice = 0

    // COUPON
    var availableCouponWalletResponses = mutableListOf<AvailableCouponWallet>()
    var availableCouponCount = 0

    inner class AvailablePoint : Serializable {
        var preAvailablePoint = 0
        var availableFreePoint = 0
        var availablePaidPoint = 0
        var availableTotalPoint = 0
    }
}