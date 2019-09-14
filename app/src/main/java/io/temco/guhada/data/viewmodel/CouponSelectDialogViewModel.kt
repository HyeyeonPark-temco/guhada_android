package io.temco.guhada.data.viewmodel

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.coupon.AvailableCouponWallet
import io.temco.guhada.data.model.order.Order
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.server.OrderServer

class CouponSelectDialogViewModel : BaseObservable() {
    var mCartIdList = intArrayOf()
    var mOrder: MutableLiveData<Order> = MutableLiveData()
    val mCouponWalletMap = mutableMapOf<String, MutableList<AvailableCouponWallet>>()
    var mCouponWalletList = mutableListOf<AvailableCouponWallet>()
    //    var mProductList : ArrayList<BaseProduct> = arrayListOf()
    var mOptionMap = hashMapOf<Long, String>()
    var mProductList = mutableListOf<BaseProduct>()

    fun getOrderForm() {
        ServerCallbackUtil.callWithToken(task = {
            OrderServer.getOrderForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mOrder.postValue(it.data as Order)
                        })
            }, accessToken = it, cartIdList = mCartIdList)
        })
    }
}