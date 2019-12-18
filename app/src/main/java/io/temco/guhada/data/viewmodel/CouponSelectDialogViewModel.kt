package io.temco.guhada.data.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.coupon.AvailableCouponWallet
import io.temco.guhada.data.model.coupon.CouponInfo
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.model.order.Order
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.server.OrderServer

class CouponSelectDialogViewModel : BaseObservable() {
    class CouponFlag {
        val NOT_SELECT_COUPON_ID: Long = -1
        val NOT_SELECT_COUPON_NUMBER = "NOT_SELECT"
    }

//    var mOrder: MutableLiveData<Order> = MutableLiveData()
    var mProductList = mutableListOf<BaseProduct>()
    var mSelectedCouponMap = hashMapOf<Long, CouponWallet?>()  // dealId, couponNumber
    var mSelectedCouponInfo = ObservableField(hashMapOf<String?, Any?>())
        @Bindable
        get() = field

    var mSelectedDealId = 0L
    var mTotalDiscountPrice = ObservableInt(0)
        @Bindable
        get() = field

    var mSelectedCoupon = ObservableField<CouponWallet>(CouponWallet().apply { this.couponId = -1 })
        @Bindable
        get() = field

    var mCouponInfo = CouponInfo()
}