package io.temco.guhada.data.model.coupon

import android.os.Parcel
import android.os.Parcelable

/**
 * 쿠폰 model
 * @see SaveTargetType
 * @author Hyeyeon Park
 */
class Coupon() : Parcelable {
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
    var maximumDiscountPrice: String? = ""  // 정액인 경우, 최대 할인가능 금액

    // DATE
    var startAt: String? = ""
    var endAt: String? = ""

    var createdAt: String? = ""
    var expireDueDay: Int = 0

    // SELLER
    var sellerId: Long? = 0
    var sellerImgUrl: String? = ""
    var sellerName: String? = ""

    enum class DiscountType(val type : String){
        RATE("RATE"),   // 정률
        PRICE("PRICE")  // 정액
    }

    constructor(parcel: Parcel) : this() {
        userId = parcel.readLong()
        serviceType = parcel.readString()
        applyType = parcel.readString()
        couponId = parcel.readValue(Long::class.java.classLoader) as? Long
        couponSaveId = parcel.readValue(Long::class.java.classLoader) as? Long
        couponNumber = parcel.readString()?:""
        couponTitle = parcel.readString()
        couponType = parcel.readString()
        saveType = parcel.readString()?:""
        status = parcel.readString()
        discountType = parcel.readString()
        discountRate = parcel.readDouble()
        discountPrice = parcel.readInt()
        minimumPrice = parcel.readInt()
        maximumDiscountPrice = parcel.readString()
        startAt = parcel.readString()
        endAt = parcel.readString()
        createdAt = parcel.readString()
        expireDueDay = parcel.readInt()
        sellerId = parcel.readValue(Long::class.java.classLoader) as? Long
        sellerImgUrl = parcel.readString()
        sellerName = parcel.readString()
    }


    // PARCELABLE
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
        dest?.writeString(maximumDiscountPrice)
        dest?.writeString(startAt)
        dest?.writeString(endAt)
        dest?.writeString(createdAt)
        dest?.writeInt(expireDueDay)
        dest?.writeValue(sellerId)
        dest?.writeString(sellerImgUrl)
        dest?.writeString(sellerName)
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