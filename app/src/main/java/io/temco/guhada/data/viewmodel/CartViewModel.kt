package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.internal.LinkedTreeMap
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.callWithToken
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.executeByResultCode
import io.temco.guhada.common.util.ToastUtil
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

    // OPTION
    var cartOptionList: MutableLiveData<MutableList<CartOption>> = MutableLiveData()
    var shownMenuPos = -1

    // OPTION ATTR
    var selectedOptionMap: MutableMap<String, String> = mutableMapOf()
    var cartDealOptionList: MutableList<CartDealOption> = mutableListOf()


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
                            cartOptionList.postValue(contractOptionAttr(cartItemId, it.data as List<LinkedTreeMap<String, Any>>))
                        })
            }, accessToken, cartItemId)
        })
    }

    // display option
    private fun contractOptionAttr(cartItemId: Long, optionInfoList: List<LinkedTreeMap<String, Any>>): MutableList<CartOption> {
        val cartDealOption = CartDealOption().apply { this.cartItemId = cartItemId }
        val cartOptionList = mutableListOf<CartOption>()
        val label1Option = CartOption()
        val label2Option = CartOption()
        val label3Option = CartOption()

        for (item in optionInfoList) {
            contractDealOptionId(cartDealOption, item)
            getCartOption(label1Option, item, optionLabel = "label1", attribute = "attribute1", colorRgb = "rgb1")
            getCartOption(label2Option, item, optionLabel = "label2", attribute = "attribute2", colorRgb = "rgb2")
            getCartOption(label3Option, item, optionLabel = "label3", attribute = "attribute3", colorRgb = "rgb3")
        }

        if (label1Option.label != null && label1Option.label?.isNotBlank() == true && label1Option.label != "null") cartOptionList.add(label1Option)
        if (label2Option.label != null && label2Option.label?.isNotBlank() == true && label2Option.label != "null") cartOptionList.add(label2Option)
        if (label3Option.label != null && label3Option.label?.isNotBlank() == true && label3Option.label != "null") cartOptionList.add(label3Option)

        cartDealOptionList.add(cartDealOption)
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

    private fun getCartOption(cartOption: CartOption, item: LinkedTreeMap<String, Any>, optionLabel: String, attribute: String, colorRgb: String): CartOption? {
        OptionAttr().apply {
            label = item[optionLabel].toString()
            name = item[attribute].toString()
            rgb = item[colorRgb].toString()
        }.let {
            cartOption.label = item[optionLabel].toString()
            return if (checkIsNotExist(cartOption.attrList, it)) {
                cartOption.attrList.add(it)
                cartOption
            } else {
                null
            }
        }
    }

    //
    private fun contractDealOptionId(cartDealOption: CartDealOption, item: LinkedTreeMap<String, Any>) {
        DealOption().apply {
            this.dealOptionId = (item["dealOptionSelectId"] as Double).toInt()

            if (item["label1"].toString() != "null")
                this.optionMap[item["label1"].toString()] = item["attribute1"].toString()

            if (item["label2"].toString() != "null")
                this.optionMap[item["label2"].toString()] = item["attribute2"].toString()

            if (item["label3"].toString() != "null")
                this.optionMap[item["label3"].toString()] = item["attribute3"].toString()
        }.let {
            cartDealOption.dealOptionList.add(it)
        }
    }

    fun onSelectAttr(optionAttr: OptionAttr) {
        selectedOptionMap[optionAttr.label] = optionAttr.name
    }

    fun updateCartItemOption(cartItemId: Long, selectDealOptionId: Int, quantity: Int) {
        callWithToken(task = { accessToken ->
            OrderServer.updateCartItemOption(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            ToastUtil.showMessage("상품 옵션이 변경되었습니다.")
                            cartResponse = it.data as CartResponse
                            if (cartResponse.cartItemResponseList.isNotEmpty()) {
                                notifyPropertyChanged(BR.cartResponse)
                            }
                        })
            }, accessToken = accessToken, cartItemId = cartItemId, selectDealOptionId = selectDealOptionId, quantity = quantity)
        })
    }

    /**
     * 장바구니 상품 dealOptionId 조회 용 클래스
     */
    inner class CartDealOption {
        var cartItemId: Long = 0
        var dealOptionList: MutableList<DealOption> = mutableListOf()
    }

    inner class DealOption {
        var dealOptionId = 0
        var optionMap: MutableMap<String, String> = mutableMapOf()
    }
}