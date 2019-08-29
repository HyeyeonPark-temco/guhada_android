package io.temco.guhada.data.model.coupon

/**
 * 쿠폰 model
 * @author Hyeyeon Park
 */
class Coupon {
    var userId: Long = 0L
    var serviceType: String? = ""
    var applyType: String? = ""

    // COUPON
    var couponId: Long? = 0L
    var couponSaveId: Long? = 0L
    var couponNumber = ""
    var couponTitle: String? = ""
    var couponType: String? = ""
    var saveType = ""
    var status: String? = ""

    // PRICE
    var discountType: String? = ""
    var discountRate: Double = 0.0
    var discountPrice: Int = 0
    var minimumPrice: Int = 0
    var maximumDiscountPrice: String? = ""

    // DATE
    var startAt: String? = ""
    var endAt: String? = ""
    var createdAt: String? = ""
    var expireDueDay: Int = 0

    // SELLER
    var sellerId: Long? = 0
    var sellerImgUrl: String? = ""
    var sellerName: String? = ""
}