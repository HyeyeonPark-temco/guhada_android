package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.callWithToken
import io.temco.guhada.data.model.CartResponse
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CartViewModel : BaseObservableViewModel() {
    var cartResponse: CartResponse = CartResponse()
        @Bindable
        get() = field

    var totalDiscountVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var selectedCount = ObservableInt(0)
        @Bindable
        get() = field

    var totalProductPrice = ObservableInt(0)
        @Bindable
        get() = field

    var shippingPrice = ObservableInt(0)
        @Bindable
        get() = field

    var totalDiscountPrice = ObservableInt(0)
        @Bindable
        get() = field

    var totalPaymentPrice = ObservableInt(0)
        @Bindable
        get() = field


    fun onClickDiscountContent() {
        totalDiscountVisible = ObservableBoolean(!totalDiscountVisible.get())
        notifyPropertyChanged(BR.totalDiscountVisible)
    }

    fun getCart(invalidTokenTask: () -> Unit = {}) {
        callWithToken({ accessToken ->
            OrderServer.getCart(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            cartResponse = it.data as CartResponse
                            if (cartResponse.cartItemResponseList.isNotEmpty()) {
                                notifyPropertyChanged(BR.cartResponse)
                            }
                        })
            }, accessToken)
        }, { invalidTokenTask() })
    }


}