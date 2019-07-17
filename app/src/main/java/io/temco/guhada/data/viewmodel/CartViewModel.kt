package io.temco.guhada.data.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.internal.LinkedTreeMap
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.callWithToken
import io.temco.guhada.data.model.cart.CartOption
import io.temco.guhada.data.model.cart.CartResponse
import io.temco.guhada.data.model.option.OptionAttr
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

    var cartOptionList: MutableLiveData<MutableList<CartOption>> = MutableLiveData()

    var shownMenuPos = -1

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

    /**
     * @exception [ClassCastException] com.google.gson.internal.LinkedTreeMap cannot be cast to io.temco.guhada.data.model.option.OptionInfo
     */
    fun getCartItemOptionList(cartItemId: Long) {
        callWithToken({ accessToken ->
            OrderServer.getCartItemOptionList(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            cartOptionList.postValue(contractOptionAttr(it.data as List<LinkedTreeMap<String, Any>>))
                        },
                        failedTask = {

                        })
            }, accessToken, cartItemId)
        })
    }

    private fun contractOptionAttr(optionInfoList: List<LinkedTreeMap<String, Any>>): MutableList<CartOption> {
        val cartOptionList = mutableListOf<CartOption>()
        val label1Option = CartOption()
        val label2Option = CartOption()
        val label3Option = CartOption()

        for (item in optionInfoList) {
            getCartOption(label1Option, item, label = "label1", attribute = "attribute1", colorRgb = "rgb1")
            getCartOption(label2Option, item, label = "label2", attribute = "attribute2", colorRgb = "rgb2")
            getCartOption(label3Option, item, label = "label3", attribute = "attribute3", colorRgb = "rgb3")
        }

        cartOptionList.add(label1Option)
        cartOptionList.add(label2Option)
        cartOptionList.add(label3Option)
        return cartOptionList
    }

    /**
     * @return isNotExist (TRUE: not exist, FALSE: exist)
     */
    private fun checkIsNotExist(optionAttrList: MutableList<OptionAttr>, optionAttr: OptionAttr): Boolean {
        for (item in optionAttrList) {
            if (item.name == optionAttr.name && item.rgb == optionAttr.rgb)
                return false
        }
        return true
    }

    private fun getCartOption(cartOption: CartOption, item: LinkedTreeMap<String, Any>, label: String, attribute: String, colorRgb: String): CartOption? {
        OptionAttr().apply {
            name = item[attribute].toString()
            rgb = item[colorRgb].toString()
        }.let {
            cartOption.label = item[label].toString()
            return if (checkIsNotExist(cartOption.attrList, it)) {
                cartOption.attrList.add(it)
                cartOption
            } else {
                null
            }
        }
    }
}