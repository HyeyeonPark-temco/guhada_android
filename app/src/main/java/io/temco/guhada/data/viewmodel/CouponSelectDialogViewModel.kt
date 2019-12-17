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
    var mCartIdList = intArrayOf()
    var mOrder: MutableLiveData<Order> = MutableLiveData()
    val mCouponWalletMap = mutableMapOf<String, MutableList<AvailableCouponWallet>>()
    var mCouponWalletList = mutableListOf<AvailableCouponWallet>()
    var mProductList = mutableListOf<BaseProduct>()
    var mSelectedCouponMap = hashMapOf<Long, CouponWallet?>()  // dealId, couponNumber
    var mSelectedCouponInfo = hashMapOf<String, Long>()

    // PRICE
//    var mSelectedProduct = BaseProduct()
    var mSelectedDealId = 0L
    var mTotalDiscountPrice = ObservableInt(0)
        @Bindable
        get() = field

    var mSelectedCoupon = ObservableField<CouponWallet>(CouponWallet().apply { this.couponId = -1 })
        @Bindable
        get() = field

    // 쿠폰 [19.12.17]
    var mCouponInfo = CouponInfo()

    fun getOrderForm() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            OrderServer.getOrderForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { model ->
                            mOrder.postValue(model.data as Order)
                        })
            }, accessToken = accessToken, cartIdList = mCartIdList)
        })
    }

}