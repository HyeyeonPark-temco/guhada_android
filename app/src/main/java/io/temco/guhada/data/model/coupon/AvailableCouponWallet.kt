package io.temco.guhada.data.model.coupon

import android.os.Parcel
import android.os.Parcelable


/**
 * couponWalletResponseList: 해당 deal에 로그인한 유저가 사용할 수 있는 쿠폰 리스트
 * Coupon 중 사용필드: couponNumber, couponTitle, discountPrice, discountRate, discountType, maximumDiscountPrice, minimumPrice
 * @since 2019.09.10
 * @author Hyeyeon Park
 */
class AvailableCouponWallet() : Parcelable {
    var dealId = 0L
    var couponWalletResponseList = mutableListOf<CouponWallet>()

    constructor(parcel: Parcel) : this() {
        dealId = parcel.readLong()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(dealId)
        dest?.writeList(couponWalletResponseList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AvailableCouponWallet> {
        override fun createFromParcel(parcel: Parcel): AvailableCouponWallet {
            return AvailableCouponWallet(parcel)
        }

        override fun newArray(size: Int): Array<AvailableCouponWallet?> {
            return arrayOfNulls(size)
        }
    }
}