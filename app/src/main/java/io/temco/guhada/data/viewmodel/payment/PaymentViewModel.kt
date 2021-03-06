package io.temco.guhada.data.viewmodel.payment

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.PaymentWayType
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.executeByResultCode
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.coupon.CouponInfo
import io.temco.guhada.data.model.order.*
import io.temco.guhada.data.model.payment.CalculatePaymentInfo
import io.temco.guhada.data.model.payment.PGAuth
import io.temco.guhada.data.model.payment.PGResponse
import io.temco.guhada.data.model.point.ExpectedPointResponse
import io.temco.guhada.data.model.point.PointProcessParam
import io.temco.guhada.data.model.point.PointRequest
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.data.server.GatewayServer
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

    var selectedMethod: PaymentMethod = PaymentMethod()
    var selectedShippingAddress: UserShipping? = UserShipping() // 선택된 배송지
    var selectedShippingMessage = ObservableField<ShippingMessage>()
        @Bindable
        get() = field

    var product: BaseProduct = BaseProduct()
        set(value) {
            field = value
            addCartItem()

//            // 스피너 교체 후 미사용
//            field.optionMap["COLOR"].let { if (it != null) optionStr += "${it.name}, " }
//            field.optionMap["SIZE"].let { if (it != null) optionStr += "${it.name}, " }
//            optionStr += "${field.totalCount} ${BaseApplication.getInstance().getString(R.string.common_unit_product)}"
        }
    var totalCount = ObservableInt(0)
        @Bindable
        get() = field
    var productList = arrayListOf<BaseProduct>() // 상품 리스트 노출
    lateinit var pgResponse: PGResponse
    var pgAuth = PGAuth()

    lateinit var cart: Cart
    var quantity: Int = 1
    var optionStr: String = ""
    var holdingPoint: Long = 0
    var usedPointNumber: Long = 0
        set(value) {
            // 할인 금액 계산
            val prev = usedPointNumber.toInt()
            val new = if (value > holdingPoint) holdingPoint.toInt() else value.toInt()
            mTotalDiscountPrice = ObservableInt(mTotalDiscountPrice.get() - prev + new)
            notifyPropertyChanged(BR.mTotalDiscountPrice)

            // 최종 결제 금액 계산
            mTotalPaymentPrice = ObservableInt(order.totalProdPrice - mTotalDiscountPrice.get())
            notifyPropertyChanged(BR.mTotalPaymentPrice)

            field = if (value > holdingPoint) {
                listener.showMessage(String.format(BaseApplication.getInstance().resources.getString(R.string.payment_message_maxusagepoint), holdingPoint))
                holdingPoint
            } else {
                value
            }

            // 적립 예정 포인트 조회
            getDueSavePoint()

            // 결제 금액 조회
            if (mSelectedCouponArray.isNotEmpty() && prev != value.toInt())
                getCalculatePaymentInfo()
        }
    var usedPoint: ObservableField<String> = ObservableField("")
        @Bindable
        get() {
            return if (usedPointNumber >= 0) {
                ObservableField(NumberFormat.getIntegerInstance().format(usedPointNumber))
            } else {
                field
            }
        }
        set(value) {
            field = value
            notifyPropertyChanged(BR.usedPoint)
        }
    var order = Order()
        @Bindable
        get() = field
        set(value) {
            this.shippingMessages = value.shippingMessage
            notifyPropertyChanged(BR.shippingMessages)

            // default 최종 결제 금액
            this.mTotalPaymentPrice = ObservableInt(value.totalPaymentPrice)
            notifyPropertyChanged(BR.mTotalPaymentPrice)

            // 본인인증 여부 판별
            checkValidation()

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
            return if (order.shippingAddress != null) "[${order.shippingAddress?.zip}] ${order.shippingAddress?.roadAddress} ${order.shippingAddress?.detailAddress}"
            else BaseApplication.getInstance().getString(R.string.payment_text_emptyshippingaddress)
        }

    var termsChecked = ObservableBoolean(false)
        @Bindable
        get() = field

    // 상품 리스트
    var cartIdList: MutableList<Int> = mutableListOf()

    var mVerifyTask: () -> Unit = {}

    // 현금영수증
    var mIsRecipientIssued = false
    var mRecipientByPhone = ObservableBoolean(true)
        @Bindable
        get() = field

    var mRecipientAvailable = ObservableBoolean(false)
        @Bindable
        get() = field

    var mRequestOrder = RequestOrder()

    var mRecipientPhone1 = ""
    var mRecipientPhone2 = ""
    var mRecipientPhone3 = ""

    var mRecipientIdentification1 = ""
    var mRecipientIdentification2 = ""

    var mRecipientCorporation1 = ""
    var mRecipientCorporation2 = ""
    var mRecipientCorporation3 = ""

    // 쿠폰, 포인트
    var mCouponInfo = MutableLiveData<CouponInfo>() // 사용 가능 쿠폰 정보
    var mSelectedCouponArray = mutableListOf<RequestOrder.CartItemPayment>() // dealId, couponNumber
    var mTotalDiscountPrice = ObservableInt(0)  // 상품 할인 금액 + 쿠폰 할인 금액 + 포인트 사용 금액
        @Bindable
        get() = field
    var mTotalPaymentPrice = ObservableInt(0)   // 최종 결제 금액
        @Bindable
        get() = field
    var mCouponDiscountPrice = 0
    var mExpectedPoint: ExpectedPointResponse = ExpectedPointResponse()
        @Bindable
        get() = field

    var mMobileVerification = ObservableBoolean(false)
        @Bindable
        get() = field
    var mEmailVerification = ObservableBoolean(false)
        @Bindable
        get() = field

    // 하단 결제 금액 계산
    var mCalculatePaymentInfo = MutableLiveData<CalculatePaymentInfo>()

    fun addCartItem() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            OrderServer.addCartItem(OnServerListener { success, o ->
                if (success) {
                    val resultCode = (o as BaseModel<*>).resultCode
                    if (resultCode == ResultCode.SUCCESS.flag) {
                        this.cart = o.data as Cart
                        if (this.cart.cartValidStatus.status) {
                            getOrderForm(accessToken)
                            BaseApplication.getInstance().plusCartCount()
                        } else {
                            listener.showMessage(this.cart.cartValidStatus.cartErrorMessage)
                            listener.closeActivity()
                        }

                        // 상품상세-바로구매 시
                        if (cartIdList.isEmpty())
                            cartIdList.add(cart.cartItemId.toInt())

                        //  getCalculatePaymentInfo()
                        getInitialCouponInfo()
                    } else {
                        listener.showMessage(o.message ?: "주문서 조회 오류")
                        listener.closeActivity()
                    }
                } else {
                    val json = JsonParser().parse(o as String)
                    val model = Gson().fromJson(json, BaseModel::class.java)
                    listener.showMessage(model.message ?: "주문서 조회 오류")
                    listener.closeActivity()
                }
                listener.hideLoadingIndicator()
            }, accessToken = accessToken, dealId = product.dealId, dealOptionId = product.dealOptionId, quantity = quantity)
        })
    }

    fun getOrderForm(accessToken: String) {
        if (cartIdList.isEmpty()) {
            cartIdList = mutableListOf(cart.cartItemId.toInt())
        }
        OrderServer.getOrderForm(OnServerListener { success, o ->
            if (success) {
                val resultCode = (o as BaseModel<*>).resultCode
                if (resultCode == ResultCode.SUCCESS.flag) {
                    val order = o.data as Order
                    this.order = order
                    this.selectedShippingAddress = order.shippingAddress  // 임시 초기값
                    notifyPropertyChanged(BR.order)
                    notifyPropertyChanged(BR.shippingAddressText)

                    //  사용 가능 포인트
                    this.holdingPoint = order.availablePointResponse.availableTotalPoint.toLong()
                    this.mTotalDiscountPrice = ObservableInt(order.totalDiscountDiffPrice)
                    notifyPropertyChanged(BR.mTotalDiscountPrice)

                    //  적립 예정 포인트 조회
                    getDueSavePoint()

                    // 사용 가능 쿠폰 정보
                    getInitialCouponInfo()
                } else {
                    listener.showMessage(o.message ?: "주문서 조회 오류")
                    listener.closeActivity()
                }
            } else {
                val json = JsonParser().parse(o as String)
                val model = Gson().fromJson(json, BaseModel::class.java)
                listener.showMessage(model.message ?: "주문서 조회 오류")
                listener.closeActivity()
            }
            listener.hideLoadingIndicator()
        }, accessToken = accessToken, cartIdList = cartIdList.toIntArray())
    }

    private fun requestOrder(accessToken: String, requestOrder: RequestOrder) {
        OrderServer.requestOrder(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = {
                        this.pgResponse = (o as BaseModel<*>).data as PGResponse
                        listener.redirectPaymentWebViewActivity()
                    },
                    dataIsNull = {
                        if (it is BaseModel<*>)
                            ToastUtil.showMessage(it.message)
                        else
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
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
        ServerCallbackUtil.callWithToken(task = { accessToken ->
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
        })
    }

    private fun setOrderCompleted(purchaseId: Double) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
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
        })
    }

    private fun saveShippingAddress() {
        ServerCallbackUtil.callWithToken(task = { token ->
            if (this@PaymentViewModel.selectedShippingAddress?.addList == true) {
                val userId = JWT(token.split("Bearer ")[1]).getClaim("userId").asInt()
                if (userId != null) {
                    if (selectedShippingAddress != null) {
                        UserServer.saveUserShippingAddress(OnServerListener { success, o ->
                            executeByResultCode(success, o,
                                    successTask = {
                                        if (CustomLog.flag) CustomLog.L("주문결제-배송지등록 성공")
                                        requestOrder(token, mRequestOrder)
                                    }, failedTask = { if (CustomLog.flag) CustomLog.L("주문결제-배송지등록 ${if (it is BaseModel<*>) it.message else "에러"}") })
                        }, userId, selectedShippingAddress!!)
                    }
                }
            } else {
                requestOrder(token, mRequestOrder)
            }
        })
    }

    private fun getDueSavePoint() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            val pointProcessParam = PointProcessParam()
            for (item in order.orderItemList) {
                val orderProd = OrderItemResponse().apply {
                    this.dcategoryId = item.dcategoryId
                    this.dealId = item.dealId
                    this.discountPrice = item.discountPrice
                    this.lcategoryId = item.lcategoryId
                    this.mcategoryId = item.mcategoryId
                    this.productPrice = item.sellPrice
                    this.scategoryId = item.scategoryId
                    this.orderProdList.add(OrderItemResponse.OrderOption().apply {
                        this.price = item.itemOptionResponse?.price ?: 0
                    })
                }

                val bundle = PointProcessParam.PointBundle().apply {
                    this.bundlePrice = item.shipExpense
                    if (quantity > 1)
                        for (i in 0 until quantity - 1)
                            this.orderProdList.add(orderProd)
                    else
                        this.orderProdList.add(orderProd)
                }

                pointProcessParam.bundleList.add(bundle)
            }

            if (pointProcessParam.bundleList.isNotEmpty()) {
                pointProcessParam.consumptionPoint = usedPointNumber.toInt()
                pointProcessParam.consumptionType = PointProcessParam.PointConsumption.BUY.type
                pointProcessParam.pointType = PointProcessParam.PointSave.BUY.type
                pointProcessParam.serviceType = PointRequest.ServiceType.AOS.type

                BenefitServer.getDueSavePoint(listener = OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                val expectedPointResponse = it.data as ExpectedPointResponse
                                this.mExpectedPoint = expectedPointResponse
                            },
                            dataIsNull = {
                                if (it is BaseModel<*>) ToastUtil.showMessage(it.message)
                                else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_error))
                            })
                }, accessToken = accessToken, pointProcessParam = pointProcessParam)
            }
        })
    }

    fun getCalculatePaymentInfo() {
        val jsonObject = JsonObject()
        jsonObject.add("cartItemPayments", JsonParser().parse(Gson().toJson(mSelectedCouponArray)))
        jsonObject.addProperty("consumptionPoint", usedPointNumber)
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            OrderServer.getCalculatePaymentInfo(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { this.mCalculatePaymentInfo.postValue((o as BaseModel<*>).data as CalculatePaymentInfo) },
                        dataIsNull = {
                            if (it is BaseModel<*>) ToastUtil.showMessage(it.message)
                            else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                        })
            }, jsonObject = jsonObject, accessToken = accessToken)
        })
    }

    /**
     * 사용 가능 쿠폰 정보
     * @author Hyeyeon Park
     */
    private fun getInitialCouponInfo() {
        ServerCallbackUtil.callWithToken(task = {
            GatewayServer.getCouponInfo(OnServerListener { success, o ->
                if (success && (o as BaseModel<*>).resultCode == ResultCode.SUCCESS.flag)
                    mCouponInfo.postValue(o.data as CouponInfo)
                else
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
            }, accessToken = it, cartItemIds = cartIdList.toIntArray())
        })
    }

    /**
     * LISTENER
     */
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

    fun onClickBuyerVerify() {}

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
        usedPointNumber = if (mTotalDiscountPrice.get() + holdingPoint > mTotalPaymentPrice.get()) {
            product.totalPrice - mCouponDiscountPrice.toLong()
        } else {
            holdingPoint
        }
        notifyPropertyChanged(BR.usedPoint)
    }

    // 결제하기 버튼 클릭
    fun onClickPay() {
        for (i in 0 until paymentWays.size)
            if (paymentWays[i])
                selectedMethod = order.paymentsMethod[i]

        if (selectedMethod.methodCode.isNotEmpty())
            if (selectedMethod.methodCode == PaymentWayType.TOKEN.code)
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_ing))
            else {
                if (this.user.get() != null)
                    when {
                        this.order.user.mobile.isNullOrEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_verifymobile))
                        this@PaymentViewModel.selectedShippingAddress == null -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_text_defaultshippingaddress))
                        else -> {
                            checkCashReceipt(nextTask = {
                                mRequestOrder.parentMethodCd = selectedMethod.methodCode
                                mRequestOrder.consumptionPoint = usedPointNumber.toInt()
                                mRequestOrder.shippingAddress = selectedShippingAddress!!
                                mRequestOrder.cartItemPayments = mSelectedCouponArray

                                checkShippingMessage()
                                saveShippingAddress()
                            })
                        }
                    }
            }
        else
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_selectpaymentway))
    }

    private fun checkCashReceipt(nextTask: () -> Unit) {
        if ((selectedMethod.methodCode != PaymentWayType.VBANK.code && selectedMethod.methodCode != PaymentWayType.DIRECT_BANK.code) ||
                (mRequestOrder.cashReceiptType.isEmpty() && mRequestOrder.cashReceiptUsage.isEmpty()) || !mIsRecipientIssued) {
            mRequestOrder.cashReceiptNo = ""
            mRequestOrder.cashReceiptType = ""
            mRequestOrder.cashReceiptUsage = ""
            nextTask()
        } else {
            when (mRequestOrder.cashReceiptUsage) {
                RequestOrder.CashReceiptUsage.PERSONAL.code ->
                    if (mRequestOrder.cashReceiptType == RequestOrder.CashReceiptType.MOBILE.code)
                        if (mRecipientPhone1.isEmpty() || mRecipientPhone2.isEmpty() || mRecipientPhone3.isEmpty()) {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_recipient1))
                            return
                        }
                RequestOrder.CashReceiptUsage.BUSINESS.code ->
                    if (mRecipientCorporation1.isEmpty() || mRecipientCorporation2.isEmpty() || mRecipientCorporation3.isEmpty()) {
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_recipient3))
                        return
                    }
            }

            mRequestOrder.cashReceiptNo = when (mRequestOrder.cashReceiptType) {
                RequestOrder.CashReceiptType.MOBILE.code -> "$mRecipientPhone1$mRecipientPhone2$mRecipientPhone3"
                RequestOrder.CashReceiptType.BUSINESS.code -> "$mRecipientCorporation1$mRecipientCorporation2$mRecipientCorporation3"
                else -> ""
            }
            nextTask()
        }
    }

    private fun checkShippingMessage() {
        val isDirect = selectedShippingMessage.get()?.message == shippingMessages[shippingMessages.size - 1].message
        val message = if (isDirect) shippingMessage else selectedShippingMessage.get()?.message
                ?: ""
        selectedShippingMessage = ObservableField(ShippingMessage().apply { this.message = message })
        mRequestOrder.shippingAddress.shippingMessage = message
    }

    /**
     *  본인인증, 이메일 인증 확인
     *  Call users API > Check emailVerify field > Call users/identity-verify API > Check mobileVerified (200: verified)
     *  @author Hyeyeon Park
     */
    private fun checkValidation() {
        Preferences.getToken().accessToken.let { token ->
            if (!token.isNullOrEmpty()) {
                val userId = JWT(token).getClaim("userId").asInt()
                if (userId != null && userId > 0)
                    UserServer.getUserById(OnServerListener { success, o ->
                        if (success && (o as BaseModel<*>).resultCode == ResultCode.SUCCESS.flag) {
                            if (CustomLog.flag) CustomLog.L("checkValidation", o.data as User)

                            val user = o.data as User
                            val diCode = user.userDetail.diCode
                            this.mEmailVerification.set(user.emailVerify)

                            if (!diCode.isNullOrEmpty()) {
                                val jsonObject = JsonObject()
                                jsonObject.addProperty("diCode", diCode)
                                UserServer.getIdentityVerify(OnServerListener { success, o ->
                                    if (success) {
                                        this.mMobileVerification = if ((o as BaseModel<*>).resultCode == ResultCode.SUCCESS.flag) ObservableBoolean(true)
                                        else ObservableBoolean(false)
                                        notifyPropertyChanged(BR.mMobileVerification)
                                    }
                                }, jsonObject = jsonObject)
                            }
                        }
                    }, userId = userId)
            }
        }

        notifyPropertyChanged(BR.mEmailVerification)
    }

    // 본인인증
    fun onClickVerify() = mVerifyTask()

    fun onClickChangeShippingAddress() = listener.redirectShippingAddressActivity()

    fun onTermsChecked(checked: Boolean) {
        this.termsChecked = ObservableBoolean(checked)
        notifyPropertyChanged(BR.termsChecked)
    }

}