package io.temco.guhada.data.model.coupon

/**
 * 쿠폰 사용 model
 * @author Hyeyeon Park
 * @since 2019.09.10
 */
class CouponConsumption {
    var consumptionTargetId = 0L
    var consumptionTargetType = ""

    var couponNumber = ""
    var dealId = 0L
    var sellerId = 0L
    var serviceType = ""

    // CATEGORY
    var dcategoryId = 0L
    var lcategoryId = 0L
    var mcategoryId = 0L
    var scategoryId = 0L

    // SAVE TARGET
    var saveTargetId = 0L
    var saveTargetType = ""

    // PRICE
    var price = 0
    var totalDiscountPrice = 0

    enum class ConsumptionTargetType(val type : String){
        USER("USER"),
        FOLLOW("FOLLOW"),
        REVIEW("REVIEW"),
        ORDER_PROD_GROUP("ORDER_PROD_GROUP"),
        BBS("BBS"),
        COMMENT("COMMENT")
    }
    /**
     * 쿠폰 사용 body
     * @author Hyeyeon Park
     * @since 2019.09.10
     */
    class CouponConsumptionRequest {
        var couponConsumptionProcessParamList = mutableListOf<CouponConsumption>()
    }
}
