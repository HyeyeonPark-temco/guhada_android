package io.temco.guhada.data.model.coupon

import com.google.gson.annotations.Expose

/**
 * 쿠폰 발급 model
 * @author Hyeyeon Park
 * @since 2019.09.10
 */
class CouponSaveProcess {
    // required
    var dcategoryId = 0L
    var lcategoryId = 0L
    var mcategoryId = 0L
    var scategoryId = 0L

    var sellerId = 0L
    var dealId = 0L
    var keyId = 0L      // dealId
    var saveActionType = ""
    var serviceType = ""


    // optional
    var expirePeriod = 0L
    var userId = 0L
    var discountPrice = 0
    var discountRate = 0.0
    var maximumDiscountPrice = 0
    var minimumPrice = 0
    var paymentPrice = 0
    var totalDiscountPrice = 0
    var discountType = ""

    @Expose
    var couponId = 0L


}
