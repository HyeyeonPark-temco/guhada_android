package io.temco.guhada.data.viewmodel

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
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
    var purchaseOrderResponse = MutableLiveData<PurchaseOrderResponse>()
    var user: ObservableField<User> = ObservableField(User())
        @Bindable
        get() = field

    var shippingMemoVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var shippingMessages: MutableList<ShippingMessage> = mutableListOf()
        @Bindable
        get() = field

    var shippingMessage = "" // 결제 요청 시 보내는 메세지
        @Bindable
        get() = field

    var selectedMethod: Order.PaymentMethod = Order.PaymentMethod()
    var selectedShippingAddress: UserShipping? = UserShipping()
    var selectedShippingMessage = ObservableField<ShippingMessage>(ShippingMessage().apply { this.message = BaseApplication.getInstance().getString(R.string.payment_text_defaultshippingaddress) }) // 스피너 표시 메세지
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
    lateinit var pgResponse: PGResponse
    var pgAuth = PGAuth()

    lateinit var cart: Cart
    var quantity: Int = 1
    var optionStr: String = ""
    var holdingPoint: Long = 0
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
        set(value) {
            this.shippingMessages = value.shippingMessage
            notifyPropertyChanged(BR.shippingMessages)
            field = value
        }
//        set(value) {
//            val list = mutableListOf<ShippingMessage>()
//            for (type in value.shippingMessage) {
//                val message = when (type) {
//                    "BEFORE_CALL" -> BaseApplication.getInstance().getString(R.string.shippingmemo_before_call)
//                    "SECURITY" -> BaseApplication.getInstance().getString(R.string.shippingmemo_security)
//                    "CALL_IF_ABSENT" -> BaseApplication.getInstance().getString(R.string.shippingmemo_call_if_absent)
//                    "PUT_DOOR" -> BaseApplication.getInstance().getString(R.string.shippingmemo_put_door)
//                    "POSTBOX" -> BaseApplication.getInstance().getString(R.string.shippingmemo_postbox)
//                    "SELF" -> BaseApplication.getInstance().getString(R.string.shippingmemo_self)
//                    else -> "기타"
//                }
//
//                list.add(ShippingMessage().apply {
//                    this.message = message
//                    this.type = type
//                })
//            }
//            value.shippingMessageType = list
//            field = value
//        }

    var productVisible = ObservableBoolean(true)
        @Bindable
        get() = field

    var discountInfoVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var savePointInfoVisible = ObservableBoolean(false)
        @Bindable
        get() = field
    var paymentWays = arrayOf(false, false, false, false)
        @Bindable
        get() = field

    val shippingAddressText: String
        @Bindable
        get() {
            return if (order.shippingAddress != null) "[${order.shippingAddress?.zip}] ${order.shippingAddress?.roadAddress}${order.shippingAddress?.detailAddress}"
            else "등록된 배송지가 없습니다"
        }

    var termsChecked = false

    fun addCartItem(accessToken: String) {
        OrderServer.addCartItm(OnServerListener { success, o ->
            if (success) {
                this.cart = (o as BaseModel<*>).data as Cart
                getOrderForm(accessToken)
                Log.e("cartItemId", cart.cartItemId.toString())
            } else {
                listener.hideLoadingIndicator()
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
            executeServerResponse(success, o, "orderForm 오류") {
                this.order = (o as BaseModel<*>).data as Order
                this.selectedShippingAddress = order.shippingAddress  // 임시 초기값
                notifyPropertyChanged(BR.order)
                notifyPropertyChanged(BR.shippingAddressText)
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

    private fun requestOrder(accessToken: String, requestOrder: RequestOrder) {
        OrderServer.requestOrder(OnServerListener { success, o ->
            executeServerResponse(success, o, "requestOrder 오류") {
                // PG사 요청
                this.pgResponse = (o as BaseModel<*>).data as PGResponse
                listener.redirectPayemntWebViewActivity()
            }
        }, accessToken, requestOrder)
    }

    /**
     * ServerListener response
     */
    private fun executeServerResponse(isSuccess: Boolean, o: Any?, failedMessage: String, successTask: () -> Unit) {
        if (isSuccess) {
            val model = (o as BaseModel<*>)
            when (model.resultCode) {
                200 -> successTask()
                else -> listener.showMessage("[${model.resultCode}]${model.message}")
            }

        } else {
            if (o != null) {
                listener.showMessage(o as String)
            } else {
                listener.showMessage(failedMessage)
            }
        }
        listener.hideLoadingIndicator()
    }

    fun callWithToken(task: (accessToken: String) -> Unit) {
        Preferences.getToken().let { token ->
            if (token != null && token.accessToken != null) {
                task("Bearer ${token.accessToken}")
            } else {
                listener.redirectLoginActivity()
                listener.showMessage("로그인이 필요한 서비스입니다.")
            }
        }
    }

    fun setOrderApproval() {
        callWithToken { accessToken ->
            OrderServer.setOrderApproval(OnServerListener { success, o ->
                if (success) {
                    val model = o as BaseModel<*>
                    if (model.resultCode == 200) {
                        val purchaseId = o.data as Double // double로 내려옴
                        this@PaymentViewModel.setOrderCompleted(purchaseId)
                    } else {
                        listener.showMessage(if (o.message == null) o.data.toString() else o.message)
                    }
                } else {
                    listener.showMessage("ORDER APPROVAL ERROR")
                }

                listener.hideLoadingIndicator()
            }, accessToken, pgAuth)
        }
    }

    private fun setOrderCompleted(purchaseId: Double) {
        callWithToken { accessToken ->
            OrderServer.setOrderCompleted(OnServerListener { success, o ->
                if (success) {
                    val model = o as BaseModel<*>
                    if (model.resultCode == 200) {
                        this.purchaseOrderResponse.postValue(model.data as PurchaseOrderResponse)
                    } else {
                        listener.showMessage(o.message)
                    }
                } else {
                    listener.showMessage("ORDER APPROVAL ERROR")
                }
            }, accessToken, purchaseId)
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

    // 결제하기 버튼 클릭
    fun onClickPay() {
        val defaultShippingMessage = BaseApplication.getInstance().getString(R.string.payment_text_defaultshippingaddress)
        if (selectedShippingMessage.get()?.equals(defaultShippingMessage) ?: true || shippingMessage.isEmpty()) {
            listener.showMessage("배송 메세지를 선택해주세요")
            return
        }

        if (termsChecked) {
            for (i in 0 until paymentWays.size) {
                if (paymentWays[i]) {
                    selectedMethod = order.paymentsMethod[i]
                }
            }

            if (selectedMethod.methodCode.isNotEmpty()) {
                if (selectedMethod.methodCode == "TOKEN") {
                    listener.showMessage("준비중입니다.")
                } else {
                    if (this.user.get() != null) {
                        if (this@PaymentViewModel.selectedShippingAddress == null) {
                            listener.showMessage("배송지를 선택해주세요.")
                        } else {
                            this@PaymentViewModel.selectedShippingAddress?.shippingMessage = if (shippingMessage.isEmpty()) selectedShippingMessage.get()?.message
                                    ?: ""
                            else shippingMessage

                            this@PaymentViewModel.selectedShippingAddress?.shippingMessageType = selectedShippingMessage.get()?.type
                                    ?: ""
                            RequestOrder().apply {
                                this.user = this@PaymentViewModel.user.get()!!
                                this.shippingAddress = this@PaymentViewModel.selectedShippingAddress!!
                                this.cartItemIdList = arrayOf(this@PaymentViewModel.cart.cartItemId)
                                this.parentMethodCd = selectedMethod.methodCode
                            }.let { requestOrder ->
                                callWithToken { accessToken -> requestOrder(accessToken, requestOrder) }
                            }
                        }
                    }
                }
            } else {
                listener.showMessage("결제 수단을 선택해주세요.")
            }
        } else {
            listener.showMessage("이용 약관에 동의해주세요.")
        }
    }

    fun onClickChangeShippingAddress() {
        listener.redirectShippingAddressActivity()
    }

    fun onTermsChecked(checked: Boolean) {
        this.termsChecked = checked
    }

    fun onShippingMemoSelected(position: Int) {
        if (shippingMessages.size > position) {
            val message = shippingMessages[position].message
            selectedShippingMessage = ObservableField(ShippingMessage().apply {
                this.message = message
                this.type = shippingMessages[position].type
            })

            if (position == 5 || message == BaseApplication.getInstance().getString(R.string.shippingmemo_self)) {
                shippingMessage = ""
                this.shippingMemoVisible = ObservableBoolean(true)
            } else {
                shippingMessage = message
                this.shippingMemoVisible = ObservableBoolean(false)
            }

            notifyPropertyChanged(BR.shippingMessage)
            notifyPropertyChanged(BR.selectedShippingMessage)
            notifyPropertyChanged(BR.shippingMemoVisible)
        }
    }

    fun onClickCloseShippingMemoSpinner() {
        listener.onClickCloseShippingMemoSpinner()
    }

}