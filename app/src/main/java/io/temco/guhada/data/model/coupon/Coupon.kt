package io.temco.guhada.data.model.coupon

class Coupon {
    var couponWalletKey: CouponWalletKey = CouponWalletKey()
    var couponId: Long? = 0
    var couponSaveId: Long? = 0
    var couponType: String? = ""
    var couponTitle: String? = ""
    var discountType: String? = ""
    var discountRate: Double = 0.0
    var discountPrice: Int = 0
    var minimumPrice: Int = 0
    var maximumDiscountPrice: String? = ""
    var startAt: String? = ""
    var endAt: String? = ""
    var sellerId: Long? = 0
    var sellerImgUrl: String? = ""
    var sellerName: String? = ""
    var status: String? = ""
    var createdAt: String? = ""
    var serviceType: String? = ""
    var applyType: String? = ""

    inner class CouponWalletKey {
        var userId: Long = 0
        var couponNumber = ""
    }
}