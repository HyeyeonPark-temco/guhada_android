package io.temco.guhada.data.model.coupon

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import java.io.Serializable

/**
 * orderForm api response의 couponWalletResponseList 모델
 * Coupon과 startAt, endAt 외 모든 필드가 동일하지만, startAt, endAt의 type이 달라서 생성
 * [Coupon] startAt, endAt: String
 * [CouponWallet] startAt, endAt: intArray
 * @author Hyeyeon Park
 * @since 2019.09.11
 */
class CouponWallet : Serializable {
    var userId: Long = 0L
    var serviceType: String? = ""
    var applyType: String? = ""

    // COUPON
    var couponId: Long? = 0L
    var couponSaveId: Long? = 0L
    var couponNumber = ""
    var couponTitle: String? = ""
    var couponType: String? = ""
    var status: String? = ""
    var saveType = ""
    var saveTargetType = ""

    // PRICE
    var discountType: String? = ""          // 할인 방식 (PRICE, RATE)
    var discountRate: Double = 0.0          // 할인률 (정률인 경우)
    var discountPrice: Int = 0              // 할인 금액 (정액인 경우)
    var minimumPrice: Int = 0               // 쿠폰을 적용받기위한 최소 금액 (전체 주문금액이 아닌, 해당 상품금액에만 적용)
    var maximumDiscountPrice: Int? = 0       // 정액인 경우, 최대 할인가능 금액

    // DATE
    var startAt = intArrayOf()
    var endAt = intArrayOf()

    var startAtTimestamp = 0L
    var endAtTimestamp = 0L

    var createdAt: String? = ""
    var expireDueDay: Int = 0

    // SELLER
    var sellerId: Long? = 0
    var sellerImgUrl: String? = ""
    var sellerName: String? = ""

    override fun toString(): String {
        return "CouponWallet(couponId=$couponId, couponNumber=$couponNumber, couponTitle=$couponTitle, couponType=$couponType, discountType=$discountType, discountRate=$discountRate, discountPrice=$discountPrice)"
    }

    fun getCouponDiscountTitle(): String {
        return if (discountType == Coupon.DiscountType.RATE.type) {
            String().format(BaseApplication.getInstance().getString(R.string.couponselect_titlerate_format), discountRate * 100, couponTitle
                    ?: "", discountPrice)
        } else {
            String().format(BaseApplication.getInstance().getString(R.string.couponselect_titleprice_format), discountPrice, couponTitle
                    ?: "")
        }
    }

}