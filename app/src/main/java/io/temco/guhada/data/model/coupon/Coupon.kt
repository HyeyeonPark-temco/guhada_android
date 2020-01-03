package io.temco.guhada.data.model.coupon

import android.os.Parcel
import android.os.Parcelable

/**
 * 쿠폰 model
 * @author Hyeyeon Park
 */
open class Coupon() : Parcelable {
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
    var saveActionType = ""
    var alreadySaved = false                // 다운받았던 쿠폰인지 여부

    // PRICE
    var discountType: String? = null        // 할인 방식 (PRICE, RATE)f
    var discountRate: Double = 0.0          // 할인률 (정률인 경우)
    var discountPrice: Int = 0              // 할인 금액 (정액인 경우)
    var minimumPrice: Int = 0               // 쿠폰을 적용받기위한 최소 금액 (전체 주문금액이 아닌, 해당 상품금액에만 적용)
    var maximumDiscountPrice: Int = 0       // 정액인 경우, 최대 할인가능 금액

    // DATE
    var startAt: String? = ""
    var endAt: String? = ""

    var createdAt: String? = ""
    var expireDueDay: Int = 0

    // SELLER
    var sellerId: Long? = 0
    var sellerImgUrl: String? = ""
    var sellerName: String? = ""

    enum class DiscountType(val type: String) {
        RATE("RATE"),    // 정률
        PRICE("PRICE"),  // 정액
        NONE("NONT")    // 적용 안함
    }


    // PARCELABLE
    constructor(parcel: Parcel) : this() {
        userId = parcel.readLong()
        serviceType = parcel.readString()
        applyType = parcel.readString()
        couponId = parcel.readValue(Long::class.java.classLoader) as? Long
        couponSaveId = parcel.readValue(Long::class.java.classLoader) as? Long
        couponNumber = parcel.readString() ?: ""
        couponTitle = parcel.readString()
        couponType = parcel.readString()
        saveType = parcel.readString() ?: ""
        status = parcel.readString()
        discountType = parcel.readString()
        discountRate = parcel.readDouble()
        discountPrice = parcel.readInt()
        minimumPrice = parcel.readInt()
        maximumDiscountPrice = parcel.readInt()
        startAt = parcel.readString()
        endAt = parcel.readString()
        createdAt = parcel.readString()
        expireDueDay = parcel.readInt()
        sellerId = parcel.readValue(Long::class.java.classLoader) as? Long
        sellerImgUrl = parcel.readString()
        sellerName = parcel.readString()
        saveTargetType = parcel.readString() ?: ""
        saveActionType = parcel.readString() ?: ""
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(userId)
        dest?.writeString(serviceType)
        dest?.writeString(applyType)
        dest?.writeValue(couponId)
        dest?.writeValue(couponSaveId)
        dest?.writeString(couponNumber)
        dest?.writeString(couponTitle)
        dest?.writeString(couponType)
        dest?.writeString(saveType)
        dest?.writeString(status)
        dest?.writeString(discountType)
        dest?.writeDouble(discountRate)
        dest?.writeInt(discountPrice)
        dest?.writeInt(minimumPrice)
        dest?.writeInt(maximumDiscountPrice)
        dest?.writeString(startAt)
        dest?.writeString(endAt)
        dest?.writeString(createdAt)
        dest?.writeInt(expireDueDay)
        dest?.writeValue(sellerId)
        dest?.writeString(sellerImgUrl)
        dest?.writeString(sellerName)
        dest?.writeString(saveTargetType)
        dest?.writeString(saveActionType)
    }

    override fun describeContents(): Int = 0


    companion object CREATOR : Parcelable.Creator<Coupon> {
        override fun createFromParcel(parcel: Parcel): Coupon {
            return Coupon(parcel)
        }

        override fun newArray(size: Int): Array<Coupon?> {
            return arrayOfNulls(size)
        }
    }


}