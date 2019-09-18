import io.temco.guhada.data.model.coupon.AvailableCouponWallet
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.model.order.Order
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class Test {
    class SellerDeal {
        var sellerId = 0L
        var dealList = mutableListOf<Deal>()
    }

    class Deal {
        var dealId = 0L
        var couponList = mutableListOf<Coupon>()
    }

    class Coupon {

    }

    val availableCouponWallet = AvailableCouponWallet().apply {
        this.dealId = 12613
        this.couponWalletResponseList = mutableListOf()
    }

    @Test
    fun coupon() {
        setList()

    }

    private fun setList() {
        availableCouponWallet.couponWalletResponseList.add(CouponWallet().apply {
            this.userId = 248
            this.couponNumber = "AAAAAA1111111"
            this.couponId = 2
            this.couponSaveId = null
            this.couponType = "PRODUCT"
            this.saveType = ""
            this.saveTargetType = ""
            this.discountPrice = 0
            this.startAt = intArrayOf(2019, 9, 11)
            this.endAt = intArrayOf(2020, 10, 12)
            this.couponTitle = ""
            this.discountType = "PRICE"
            this.discountRate = 0.0
            this.minimumPrice = 0
            this.maximumDiscountPrice = 10000
            this.sellerId = 251
            this.sellerImgUrl = "https://s3-ap-northeast-1.amazonaws.com/poc.online-luxury-market/images/users/normal/profile/KakaoTalk_Photo_2019-05-24-14-50-02.jpeg"
            this.sellerName = "필라테스좋아"
            this.status = "SAVED"
            this.createdAt = null
            this.applyType = "ALL"
            this.expireDueDay = 0
            this.startAtTimestamp = 1568127600000
            this.endAtTimestamp = 1568127600000
        })

        availableCouponWallet.couponWalletResponseList.add(CouponWallet().apply {
            this.userId = 248
            this.couponNumber = "BBBBBB222222"
            this.couponId = 3
            this.couponSaveId = null
            this.couponType = "PRODUCT"
            this.saveType = ""
            this.saveTargetType = ""
            this.discountPrice = 0
            this.startAt = intArrayOf(2019, 9, 11)
            this.endAt = intArrayOf(2020, 10, 12)
            this.couponTitle = ""
            this.discountType = "PRICE"
            this.discountRate = 0.0
            this.minimumPrice = 0
            this.maximumDiscountPrice = 10000
            this.sellerId = 251
            this.sellerImgUrl = "https://s3-ap-northeast-1.amazonaws.com/poc.online-luxury-market/images/users/normal/profile/KakaoTalk_Photo_2019-05-24-14-50-02.jpeg"
            this.sellerName = "필라테스좋아"
            this.status = "SAVED"
            this.createdAt = null
            this.applyType = "ALL"
            this.expireDueDay = 0
            this.startAtTimestamp = 1568127600000
            this.endAtTimestamp = 1568127600000
        })

        availableCouponWallet.couponWalletResponseList.add(CouponWallet().apply {
            this.userId = 248
            this.couponNumber = "CCCCCC333333"
            this.couponId = 4
            this.couponSaveId = null
            this.couponType = "PRODUCT"
            this.saveType = ""
            this.saveTargetType = ""
            this.discountPrice = 0
            this.startAt = intArrayOf(2019, 9, 11)
            this.endAt = intArrayOf(2020, 10, 12)
            this.couponTitle = ""
            this.discountType = "PRICE"
            this.discountRate = 0.0
            this.minimumPrice = 0
            this.maximumDiscountPrice = 10000
            this.sellerId = 251
            this.sellerImgUrl = "https://s3-ap-northeast-1.amazonaws.com/poc.online-luxury-market/images/users/normal/profile/KakaoTalk_Photo_2019-05-24-14-50-02.jpeg"
            this.sellerName = "필라테스좋아"
            this.status = "SAVED"
            this.createdAt = null
            this.applyType = "ALL"
            this.expireDueDay = 0
            this.startAtTimestamp = 1568127600000
            this.endAtTimestamp = 1568127600000
        })
    }
}