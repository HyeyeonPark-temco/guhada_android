package io.temco.guhada.data.model.coupon

import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.product.BaseProduct
import java.io.Serializable

/**
 * 주문서 쿠폰 정보
 * /benefits/order/coupon API response
 *
 * savedCouponCount: 보유 쿠폰 수
 * availableCouponCount: 사용 가능 쿠폰 수
 * totalCouponDiscountPrice: 주문서 진입 시 자동 선택 된 쿠폰의 할인 금액
 * selectedCouponCount: 주문서 진입 시 자동 선택 된 쿠폰 수
 *
 * @author Hyeyeon Park
 */
class CouponInfo : Serializable {
    var savedCouponCount = 0
    var availableCouponCount = 0
    var totalCouponDiscountPrice = 0
    var selectedCouponCount = 0
    var totalProductPrice = 0
    var benefitSellerResponseList = mutableListOf<BenefitSellerResponse>()


    class BenefitSellerResponse : Serializable {
        var sellerId = 0
        var sellerName = ""
        var benefitOrderProductResponseList = mutableListOf<BenefitOrderProductResponse>()
    }

    class BenefitOrderProductResponse : BaseProduct(), Serializable {
        var cartId:Long = 0L
        var imageUrl = ""
        var dealName = ""
        var option: String? = ""
        var currentQuantity = 0
        var totalStock = 0
        var productPrice = 0
        var orderPrice = 0
        @SerializedName(value = "benefitProductCouponResponseList", alternate = ["benefitProductCouponRespopnseList"])
        var benefitProductCouponResponseList = mutableListOf<BenefitOrderProductCouponResponse>()

    }

    /**
     * selected: 주문서-쿠폰 팝업 진입 시 자동 선택 여부
     * disable: 주문서-쿠폰 팝업 진입 시 선택 가능 여부(쿠폰 중복 선택 불가)
     *
     * @author Hyeyeon Park
     */
    class BenefitOrderProductCouponResponse : CouponWallet(), Serializable {
        var productPrice = 0
        var couponDiscountPrice = 0
        var orderPrice = 0
        var selected = false
        var disable = false
    }
}