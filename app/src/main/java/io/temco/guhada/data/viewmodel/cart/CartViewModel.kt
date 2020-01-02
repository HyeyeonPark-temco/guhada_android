package io.temco.guhada.data.viewmodel.cart

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.internal.LinkedTreeMap
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.callWithToken
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.executeByResultCode
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.cart.*
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import retrofit2.HttpException

class CartViewModel : BaseObservableViewModel() {
    var cartResponse: MutableLiveData<CartResponse> = MutableLiveData()
        @Bindable
        get() = field

    var totalDiscountVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var totalProductPrice = ObservableInt(0)
        @Bindable
        get() = field

    var totalDiscountPrice = ObservableInt(0)
        @Bindable
        get() = field

    var totalPaymentPrice = ObservableInt(0)
        @Bindable
        get() = field

    var totalShipPrice = ObservableInt(0) // 선택상품 배송비
        @Bindable
        get() = field

    // OPTION
    var cartOptionList: MutableLiveData<MutableList<CartOption>> = MutableLiveData()
    var cartOptionListForSpinner: MutableLiveData<MutableList<OptionInfo>> = MutableLiveData()

    var shownMenuPos = -1

    // OPTION ATTR
    var selectedOptionMap: MutableMap<String, String> = mutableMapOf()
    var cartDealOptionList: MutableList<CartDealOption> = mutableListOf()

    // DELETE
    var selectedCartItem: MutableList<Cart> = mutableListOf() // 결제하기 클릭 시 전달 데이터
    var selectCartItemId: MutableList<Int> = mutableListOf() // 장바구니 상품 삭제 시 사용 데이터
        @Bindable
        get() = field
        set(value) {
            field = value

            if (value.size == 0) {
                allChecked = ObservableBoolean(false)
                notifyPropertyChanged(BR.allChecked)
            } else {

            }
        }

    var allChecked = ObservableBoolean(false)
        @Bindable
        get() = field

    var notNotifyAllChecked = true // allChecked 필드 notify flag

    lateinit var clickPaymentListener: (productList: ArrayList<BaseProduct>, cartIdList: Array<Int>) -> Unit

    // 선택 삭제 클릭
    var showDeleteDialog: () -> Unit = {}

    var totalItemCount = ObservableInt(0)   // validStatus = true 상품들의 count
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalItemCount)
        }

    var mCloseActivityTask: (message: String) -> Unit = {}

    // CLICK EVENT
    fun onClickDiscountContent() {
        totalDiscountVisible = ObservableBoolean(!totalDiscountVisible.get())
        notifyPropertyChanged(BR.totalDiscountVisible)
    }

    fun onSelectAttr(optionAttr: OptionAttr) {
        selectedOptionMap[optionAttr.label] = optionAttr.name
    }

    fun onCheckedAll(checked: Boolean) {
        allChecked = ObservableBoolean(checked)
        notifyPropertyChanged(BR.allChecked)
    }

    fun onCheckedAll() {
        allChecked = ObservableBoolean(!allChecked.get())
        notifyPropertyChanged(BR.allChecked)
    }

    // 주문하기 버튼 클릭
    fun onClickPayment() = getPaymentProducts(item = selectedCartItem)

    // 바로구매 버튼 클릭
    fun onClickItemPayment(cart: Cart) = getPaymentProducts(item = cart)

    private fun getPaymentProducts(item: Any) {
        val list = arrayListOf<BaseProduct>()
        val productList: MutableList<Cart>
        val cartIdList: Array<Int>
        if (item is Cart) {
            productList = arrayListOf(item)
            cartIdList = arrayOf(item.cartItemId.toInt())
        } else {
            productList = item as MutableList<Cart>
            cartIdList = selectCartItemId.toTypedArray()

            if (cartIdList.isEmpty()) {
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_paymentempty))
                return
            }
        }

        // 주문 결제 화면으로 넘길 상품 데이터 가공
        for (cart in productList) {
            BaseProduct().apply {
                this.dealId = cart.dealId
                this.brandName = cart.brandName ?: ""
                this.season = cart.season ?: ""
                this.name = cart.dealName ?: ""
                this.totalPrice = cart.sellPrice ?: 0
                this.profileUrl = cart.imageUrl ?: ""
                this.optionStr = cart.getOptionStr() ?: ""
                this.sellPrice = cart.sellPrice
                this.discountPrice = cart.discountPrice
                this.totalCount = cart.tempQuantity
            }.let {
                list.add(it)
            }
        }

        if (::clickPaymentListener.isInitialized)
            clickPaymentListener(list, cartIdList)
    }


    fun getCart(invalidTokenTask: () -> Unit = {}) {
        callWithToken({ accessToken ->
            OrderServer.getCart(OnServerListener { success, o ->
                if (success && o is BaseModel<*>) {
                    if (o.resultCode == ResultCode.SUCCESS.flag) setCartItemList(o.data as CartResponse)
                    else mCloseActivityTask(o.message)
                } else {
                    val defaultMessage = BaseApplication.getInstance().getString(R.string.common_message_servererror)
                    val message = when (o) {
                        is Throwable -> o.message ?: defaultMessage
                        is HttpException -> o.message()
                        else -> defaultMessage
                    }
                    mCloseActivityTask(message)
                }
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
                            cartOptionList.postValue(contractOptionAttr(cartItemId, it.list as List<LinkedTreeMap<String, Any>>))
                        })
            }, accessToken, cartItemId)
        })
    }

    fun getCartItemOptionListForSpinner(cartItemId: Long) {
        callWithToken({ accessToken ->
            OrderServer.getCartItemOptionListForSpinner(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            cartOptionListForSpinner.postValue(it.data as MutableList<OptionInfo>)
                        })
            }, accessToken, cartItemId)
        })
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

    fun updateCartItemOption(cartItemId: Long, selectDealOptionId: Int, quantity: Int) {
        callWithToken(task = { accessToken ->
            OrderServer.updateCartItemOption(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_changeselectedoption))
                            setCartItemList(it.data as CartResponse)
                            selectCartItemId = mutableListOf()
                            selectedCartItem = mutableListOf()
                        })
            }, accessToken = accessToken, cartItemId = cartItemId, selectDealOptionId = selectDealOptionId, quantity = quantity)
        })
    }

    fun updateCartItemQuantity(cartItemId: Long, quantity: Int) {
        callWithToken(task = { accessToken ->
            OrderServer.updateCartItemQuantity(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_changeselectedoption))
                            setCartItemList(it.data as CartResponse)
                            selectCartItemId = mutableListOf()
                            selectedCartItem = mutableListOf()
                        })
            }, accessToken = accessToken, cartItemId = cartItemId, quantity = quantity)
        })
    }

    fun onClickDeleteCartItem() {
        if (selectCartItemId.isEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_deleteempty))
        } else {
            showDeleteDialog()
        }
    }

    fun deleteCartItem() {
        callWithToken(task = { accessToken ->
            OrderServer.deleteCartItem(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            BaseApplication.getInstance().minusCartCount(selectCartItemId.toIntArray().size)

                            // 초기화
                            selectedCartItem = mutableListOf()
                            selectCartItemId = mutableListOf()
                            allChecked = ObservableBoolean(false)
                            notifyPropertyChanged(BR.allChecked)
                            notifyPropertyChanged(BR.selectCartItemId)

                            this.cartResponse.postValue(it.data as CartResponse)
                        })
            }, accessToken = accessToken, cartItemIdList = selectCartItemId.toIntArray())
        })
    }

    private fun setCartItemList(cartResponse: CartResponse) = this.cartResponse.postValue(cartResponse)

    /**
     * CartOption 데이터 가공
     */
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

    /**
     * 선택 옵션의 dealOptionId 추출
     */
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

}