package io.temco.guhada.data.viewmodel

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.executeByResultCode
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.order.Order
import io.temco.guhada.data.model.order.PurchaseOrderResponse
import io.temco.guhada.data.model.payment.PGAuth
import io.temco.guhada.data.model.payment.PGResponse
import io.temco.guhada.data.model.order.RequestOrder
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.UserServer
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
    var selectedDiscountCoupon = ObservableField<String>(BaseApplication.getInstance().getString(R.string.payment_hint_coupon))
        @Bindable
        get() = field

    var product: BaseProduct = BaseProduct()
        set(value) {
            field = value
            field.optionMap["COLOR"].let { if (it != null) optionStr += "${it.name}, " }
            field.optionMap["SIZE"].let { if (it != null) optionStr += "${it.name}, " }
            optionStr += "${field.totalCount} ${BaseApplication.getInstance().getString(R.string.common_unit_product)}"

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
                listener.showMessage("${BaseApplication.getInstance().getString(R.string.payment_message_maxusagepoint1)} $holdingPoint ${BaseApplication.getInstance().getString(R.string.payment_message_maxusagepoint2)}")
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
            else BaseApplication.getInstance().getString(R.string.payment_text_emptyshippingaddress)
        }

    var termsChecked = false

    fun addCartItem(accessToken: String) {
        OrderServer.addCartItm(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = {
                        this.cart = (o as BaseModel<*>).data as Cart
                        getOrderForm(accessToken)
                        Log.e("cartItemId", cart.cartItemId.toString())
                    },
                    failedTask = {
                        listener.hideLoadingIndicator()
                        if (o != null) listener.showMessage(o as String)
                        else listener.showMessage("addCartItem 오류")
                    })
        }, accessToken = accessToken, productId = product.productId, optionId = product.dealOptionId, quantity = quantity)
    }

    private fun getOrderForm(accessToken: String) {
        OrderServer.getOrderForm(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = {
                        this.order = (o as BaseModel<*>).data as Order
                        this.selectedShippingAddress = order.shippingAddress  // 임시 초기값
                        notifyPropertyChanged(BR.order)
                        notifyPropertyChanged(BR.shippingAddressText)
                    },
                    failedTask = {
                        if (o != null) listener.showMessage(o as String)
                        else listener.showMessage("orderForm 오류")
                    })
            listener.hideLoadingIndicator()
        }, accessToken, arrayOf(cart.cartItemId))
    }

    private fun requestOrder(accessToken: String, requestOrder: RequestOrder) {
        OrderServer.requestOrder(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = {
                        this.pgResponse = (o as BaseModel<*>).data as PGResponse
                        listener.redirectPaymentWebViewActivity()
                    })
        }, accessToken, requestOrder)
    }

    fun callWithToken(task: (accessToken: String) -> Unit) {
        Preferences.getToken().let { token ->
            if (token != null && token.accessToken != null) {
                task("Bearer ${token.accessToken}")
            } else {
                listener.redirectLoginActivity()
                listener.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            }
        }
    }

    fun setOrderApproval() {
        callWithToken { accessToken ->
            OrderServer.setOrderApproval(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            val purchaseId = it.data as Double // double로 내려옴
                            this@PaymentViewModel.setOrderCompleted(purchaseId)
                        },
                        failedTask = {
                            listener.showMessage(if (it.message == null) it.data.toString() else it.message)
                            listener.hideLoadingIndicator()
                        })

            }, accessToken, pgAuth)
        }
    }

    private fun setOrderCompleted(purchaseId: Double) {
        callWithToken { accessToken ->
            OrderServer.setOrderCompleted(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            this.purchaseOrderResponse.postValue(it.data as PurchaseOrderResponse)
                        },
                        failedTask = {
                            listener.showMessage(it.message)
                        })
                listener.hideLoadingIndicator()
            }, accessToken, purchaseId)
        }
    }

    private fun saveShippingAddress(userId: Int) {
        if (selectedShippingAddress != null) {
            UserServer.saveUserShippingAddress(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_add_success))

                        })
            }, userId, selectedShippingAddress!!)
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
            listener.showMessage(BaseApplication.getInstance().getString(R.string.payment_hint_shippingmemo))
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
                    listener.showMessage(BaseApplication.getInstance().getString(R.string.common_message_ing))
                } else {
                    if (this.user.get() != null) {
                        if (this@PaymentViewModel.selectedShippingAddress == null) {
                            listener.showMessage(BaseApplication.getInstance().getString(R.string.payment_text_defaultshippingaddress))
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
                                val accessToken = Preferences.getToken().accessToken
                                if (this@PaymentViewModel.selectedShippingAddress?.addList == true) {
                                    // 배송지 추가
                                    val userId = JWT(accessToken).getClaim("userId").asInt()
                                    if (userId != null) saveShippingAddress(userId)
                                }

                                requestOrder("Bearer $accessToken" +
                                        "", requestOrder)
                            }
                        }
                    }
                }
            } else {
                listener.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_selectpaymentway))
            }
        } else {
            listener.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_confirmtemrs))
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