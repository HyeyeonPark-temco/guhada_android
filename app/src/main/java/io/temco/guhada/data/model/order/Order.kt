package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.coupon.Coupon

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
    var availablePointResponse = AvailablePoint()

    // PRICE
    var totalProdPrice = 0
    var totalDiscountDiffPrice = 0
    var totalShipPrice = 0
    var totalPaymentPrice = 0

    // COUPON
    var availableCouponWalletResponses = mutableListOf<AvailableCouponWallet>()

    /**
     * couponWalletResponseList: 해당 deal에 로그인한 유저가 사용할 수 있는 쿠폰 리스트
     * Coupon 중 사용필드: couponNumber, couponTitle, discountPrice, discountRate, discountType, maximumDiscountPrice, minimumPrice
     * @since 2019.09.10
     * @author Hyeyeon Park
     */
    inner class AvailableCouponWallet {
        var dealId = 0L
        var couponWalletResponseList = mutableListOf<Coupon>()
    }

    inner class AvailablePoint {
        var preAvailablePoint = 0
        var availableFreePoint = 0
        var availablePaidPoint = 0
        var availableTotalPoint = 0
    }
}