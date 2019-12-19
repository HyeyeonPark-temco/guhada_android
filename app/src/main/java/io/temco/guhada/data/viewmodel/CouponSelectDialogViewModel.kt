package io.temco.guhada.data.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.data.model.coupon.CouponInfo
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.model.product.BaseProduct

class CouponSelectDialogViewModel : BaseObservable() {
    class CouponFlag {
        val NOT_SELECT_COUPON_ID: Long = -1
        val NOT_SELECT_COUPON_NUMBER = "NOT_SELECT"
    }

    var mProductList = mutableListOf<BaseProduct>()
    var mSelectedCouponMap = hashMapOf<Long, CouponInfo.BenefitOrderProductCouponResponse?>()
    var mSelectedCouponInfo = ObservableField(hashMapOf<String?, Any?>())
        @Bindable
        get() = field

    var mSelectedDealId = 0L
    var mTotalDiscountPrice = ObservableInt(0)
        @Bindable
        get() = field
    var mTotalProductPrice = ObservableInt(0)
        @Bindable
        get() = field
    var mTotalProductCount = ObservableInt(0)
        @Bindable
        get() = field

    var mSelectedCoupon = ObservableField<CouponWallet>(CouponWallet().apply { this.couponId = -1 })
        @Bindable
        get() = field



    var mCouponInfo = CouponInfo()
}