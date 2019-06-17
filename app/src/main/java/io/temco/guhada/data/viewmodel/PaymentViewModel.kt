package io.temco.guhada.data.viewmodel

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.temco.guhada.BR
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.LoginServer
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.PaymentActivity
import java.text.NumberFormat

class PaymentViewModel(val listener: PaymentActivity.OnPaymentListener) : BaseObservableViewModel() {
    var user: ObservableField<User> = ObservableField(User())
        @Bindable
        get() = field

    var shippingAddresses: ObservableField<List<UserShipping>> = ObservableField(ArrayList())
        @Bindable
        get() = field

    var selectedShippingMessage = ObservableField<String>("배송메모를 선택해 주세요.")
        @Bindable
        get() = field

    var selectedDiscountCoupon = ObservableField<String>("적용 가능한 쿠폰을 선택해 주세요.")
        @Bindable
        get() = field

    var product: BaseProduct = BaseProduct()
        set(value) {
            field = value
            field.optionMap["COLOR"].let { if (it != null) optionStr += "${it.name}, " }
            field.optionMap["SIZE"].let { if (it != null) optionStr += "${it.name}, " }
            optionStr += "${field.totalCount}개"

            callWithToken { accessToken ->
                Log.e("AccessToken", accessToken)
                addCartItem(accessToken)
            }
        }
    lateinit var cart: Cart
    var quantity: Int = 1
    var optionStr: String = ""
    var holdingPoint: Long = 11223344
    var usedPointNumber: Long = 0
        set(value) {
            field = if (value > holdingPoint) {
                listener.showMessage("최대 사용 가능 포인트는 $holdingPoint P 입니다")
                holdingPoint
            } else {
                value
            }
        }
    var usedPoint: ObservableField<String> = ObservableField("")
        @Bindable
        get() {
            return if (usedPointNumber > 0) {
                ObservableField(NumberFormat.getIntegerInstance().format(usedPointNumber))
            } else {
                field
            }
        }
    var order: Order = Order()
        @Bindable
        get() = field

    var productVisible = ObservableBoolean(true)
        @Bindable
        get() = field

    var discountInfoVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var savePointInfoVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var textShippingMemoVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var paymentWays = arrayOf(false, false, false, false)
        @Bindable
        get() = field

    val shippingAddressText: String
        @Bindable
        get() = "[${order.shippingAddress.zip}] ${order.shippingAddress.roadAddress}${order.shippingAddress.detailAddress}"

    private fun addCartItem(accessToken: String) {
        OrderServer.addCartItm(OnServerListener { success, o ->
            if (success) {
                this.cart = (o as BaseModel<*>).data as Cart
                getOrderForm(accessToken)
                Log.e("cartItemId", cart.cartItemId.toString())
            } else {
                if (o != null) {
                    listener.showMessage(o as String)
                } else {
                    listener.showMessage("addCartItem 오류")
                }
            }
        }, accessToken = accessToken, productId = product.productId, optionId = product.dealOptionId, quantity = quantity)
    }

    private fun getOrderForm(accessToken: String) {
        OrderServer.getOrderForm(OnServerListener { success, o ->
            if (success) {
                this.order = (o as BaseModel<*>).data as Order
                notifyPropertyChanged(BR.order)
                notifyPropertyChanged(BR.shippingAddressText)
            } else {
                if (o != null) {
                    listener.showMessage(o as String)
                } else {
                    listener.showMessage("orderForm 오류")
                }
            }
        }, accessToken, arrayOf(cart.cartItemId))
    }

    private fun getUserInfo(userId: Int) {
//        val userId = JWT(token.accessToken).getClaim("userId").asInt()

        LoginServer.getUserById(OnServerListener { success, o ->
            if (success) {
                this.user = ObservableField((o as BaseModel<*>).data as User)
                notifyPropertyChanged(BR.user)
            } else {
                CommonUtil.debug(o as String)
            }
        }, userId)
    }

    private fun getUserShippingAddress(userId: Int) {
        LoginServer.getUserShippingAddress(OnServerListener { success, o ->
            if (success) {
                this.shippingAddresses = ObservableField((o as BaseModel<*>).data as List<UserShipping>)
                notifyPropertyChanged(BR.shippingAddresses)
            } else {
                CommonUtil.debug(o as String)
            }
        }, userId)
    }

    private fun callWithToken(task: (accessToken: String) -> Unit) {
        Preferences.getToken().let { token ->
            if (token != null && token.accessToken != null) {
                task("Bearer ${token.accessToken}")
            } else {

                listener.showMessage("저장된 토큰 없음")
            }
        }
    }

    // LISTENER
    fun onPaymentWayChecked(view: View, checked: Boolean) {
        val pos = view.tag?.toString()?.toInt()
        if (pos != null) {
            if (checked) {
                for (i in 0 until 4)
                    paymentWays[i] = false

                paymentWays[pos] = checked
                listener.notifyRadioButtons()
            } else {
                paymentWays[pos] = false
            }
        }
    }

    fun onClickExpandProduct() {
        productVisible = ObservableBoolean(!productVisible.get())
        notifyPropertyChanged(BR.productVisible)
    }

    fun onClickBuyerVerify() {

    }

    fun onClickSavePointInfo() {
        savePointInfoVisible = ObservableBoolean(!savePointInfoVisible.get())
        notifyPropertyChanged(BR.savePointInfoVisible)
    }

    fun onClickDiscountInfo() {
        discountInfoVisible = ObservableBoolean(!discountInfoVisible.get())
        notifyPropertyChanged(BR.discountInfoVisible)
    }

    fun onClickUsedPointView() {
        listener.setUsedPointViewFocused()
    }

    fun onClickUseAllPoint() {
        usedPointNumber = holdingPoint
        notifyPropertyChanged(BR.usedPoint)
    }

    fun onShippingMemoSelected(position: Int) {
        if (order.shippingMessage.size > position) {
            selectedShippingMessage = ObservableField(order.shippingMessage[position])
            notifyPropertyChanged(BR.selectedShippingMessage)
        }
    }
}