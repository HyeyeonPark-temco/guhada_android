package io.temco.guhada.data.viewmodel.payment

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.google.gson.*
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
import io.temco.guhada.data.model.AvailableBenefitCount
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.coupon.CouponWallet
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
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.CouponSelectDialogActivity
import io.temco.guhada.view.activity.PaymentActivity
import org.json.JSONObject
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
    var selectedShippingAddress: UserShipping? = UserShipping() // 선택된 배송메세지
    var selectedShippingMessage = ObservableField<ShippingMessage>(ShippingMessage().apply { this.message = BaseApplication.getInstance().getString(R.string.payment_hint_shippingmemo) }) // 배송메세지 스피너 표시 메세지
        @Bindable
        get() = field

    var product: BaseProduct = BaseProduct()
        set(value) {
            field = value
//            field.optionMap["COLOR"].let { if (it != null) optionStr += "${it.name}, " }
//            field.optionMap["SIZE"].let { if (it != null) optionStr += "${it.name}, " }
//            optionStr += "${field.totalCount} ${BaseApplication.getInstance().getString(R.string.common_unit_product)}"
            callWithToken { accessToken ->
                Log.e("AccessToken", accessToken)
                addCartItem(accessToken)
            }
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
//            this.mEmailVerification = ObservableBoolean(value.user.emailVerify)

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
    var mAvailableBenefitCount: MutableLiveData<AvailableBenefitCount> = MutableLiveData(AvailableBenefitCount())
    var mSelectedCouponMap: HashMap<Long, CouponWallet?> = hashMapOf() // dealId, couponWallet
    var mTotalDiscountPrice = ObservableInt(0)  // 상품 할인 금액 + 쿠폰 할인 금액 + 포인트 사용 금액
        @Bindable
        get() = field
    var mTotalPaymentPrice = ObservableInt(0)   // 최종 결제 금액
        @Bindable
        get() = field
    var mCouponDiscountPrice = 0
    var mExpectedPoint: MutableLiveData<ExpectedPointResponse> = MutableLiveData()
        @Bindable
        get() = field

    var mMobileVerification = ObservableBoolean(false)
        @Bindable
        get() = field
    var mEmailVerification = ObservableBoolean(order.user.emailVerify)
        @Bindable
        get() = field

    // 하단 결제 금액 계산
    var mCalculatePaymentInfo = MutableLiveData<CalculatePaymentInfo>(CalculatePaymentInfo())

    fun addCartItem(accessToken: String) {
        OrderServer.addCartItem(OnServerListener { success, o ->
            if (success) {
                val resultCode = (o as BaseModel<*>).resultCode
                if (resultCode == ResultCode.SUCCESS.flag) {
                    this.cart = o.data as Cart
                    if (this.cart.cartValidStatus.status)
                        getOrderForm(accessToken)
                    else {
                        listener.showMessage(this.cart.cartValidStatus.cartErrorMessage)
                        listener.closeActivity()
                    }

                    // 상품상세-바로구매 시
                    if (cartIdList.isEmpty())
                        cartIdList.add(cart.cartItemId.toInt())
                    getCalculatePaymentInfo()
                    Log.e("cartItemId", cart.cartItemId.toString())

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
    }

    /**
     * TODO 주문서 진입 시 cartValidStatus 체크해서 처리 [2019.08.09]
     */
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
                } else {
                    listener.showMessage(o.message ?: "주문서 조회 오류")
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

    private fun saveShippingAddress(accessToken: String, userId: Int) {
        if (selectedShippingAddress != null) {
            UserServer.saveUserShippingAddress(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            // ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_add_success))
                            if (CustomLog.flag) CustomLog.L("주문결제-배송지등록 success")
                            requestOrder("Bearer $accessToken", mRequestOrder)
                        }, failedTask = { if (CustomLog.flag) CustomLog.L("주문결제-배송지등록 ${if (it is BaseModel<*>) it.message else "에러"}") })
            }, userId, selectedShippingAddress!!)
        }
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
                                this.mExpectedPoint.postValue(expectedPointResponse)
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
        val jsonArray = JsonArray()
        for (item in cartIdList) {
            RequestOrder.CartItemPayment().apply {
                this.cartItemId = item.toLong()
                this.couponNumber = getCouponNumberByCartItemId(item.toLong())
            }.let {
                val element = JsonParser().parse(Gson().toJson(it))
                jsonArray.add(element)
            }
        }

        val jsonObject = JsonObject()
        jsonObject.add("cartItemPayments", jsonArray)
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
     * LISTENER
     */
    // 결제 수단 checkbox listener
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
        usedPointNumber = if (mTotalDiscountPrice.get() + holdingPoint > mTotalPaymentPrice.get()) {
            product.totalPrice - mCouponDiscountPrice.toLong()
        } else {
            holdingPoint
        }
        notifyPropertyChanged(BR.usedPoint)
    }

    // 결제하기 버튼 클릭
    fun onClickPay() {

        if (termsChecked.get()) {
            for (i in 0 until paymentWays.size)
                if (paymentWays[i])
                    selectedMethod = order.paymentsMethod[i]

            if (selectedMethod.methodCode.isNotEmpty()) {
                mRequestOrder.parentMethodCd = selectedMethod.methodCode

                if (selectedMethod.methodCode == PaymentWayType.TOKEN.code) {
                    listener.showMessage(BaseApplication.getInstance().getString(R.string.common_message_ing))
                } else {
                    if (this.user.get() != null) {
                        if (this.order.user.mobile.isNullOrEmpty()) {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_verifymobile))
                        } else if (!mEmailVerification.get()) {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_verifyemail))
                        } /*else if (selectedShippingMessage.get()?.message == defaultShippingMessage || shippingMessage.isEmpty()) {
                            listener.showMessage(BaseApplication.getInstance().getString(R.string.payment_hint_shippingmemo))
                        } */ else if (this@PaymentViewModel.selectedShippingAddress == null) {
                            listener.showMessage(BaseApplication.getInstance().getString(R.string.payment_text_defaultshippingaddress))
                        } else {
                            // 현금영수증
                            if ((selectedMethod.methodCode != PaymentWayType.VBANK.code && selectedMethod.methodCode != PaymentWayType.DIRECT_BANK.code) ||
                                    (mRequestOrder.cashReceiptType.isEmpty() && mRequestOrder.cashReceiptUsage.isEmpty()) || !mIsRecipientIssued) {
                                mRequestOrder.cashReceiptNo = ""
                                mRequestOrder.cashReceiptType = ""
                                mRequestOrder.cashReceiptUsage = ""
                            } else {
                                when (mRequestOrder.cashReceiptUsage) {
                                    RequestOrder.CashReceiptUsage.PERSONAL.code ->
                                        if (mRequestOrder.cashReceiptType == RequestOrder.CashReceiptType.MOBILE.code)
                                            if (mRecipientPhone1.isEmpty() || mRecipientPhone2.isEmpty() || mRecipientPhone3.isEmpty()) {
                                                listener.showMessage("현금영수증을 신청할 핸드폰 번호를 입력하세요")
                                                return
                                            } else if (mRecipientPhone2.length < 4 || mRecipientPhone3.length < 4) {
                                                listener.showMessage("현금영수증을 신청할 휴대폰 번호를 올바르게 입력하세요")
                                                return
                                            }
                                    RequestOrder.CashReceiptUsage.BUSINESS.code ->
                                        if (mRecipientCorporation1.isEmpty() || mRecipientCorporation2.isEmpty() || mRecipientCorporation3.isEmpty()) {
                                            listener.showMessage("현금영수증을 신청할 사업자 등록번호를 입력하세요")
                                            return
                                        }
                                }

                                mRequestOrder.cashReceiptNo = when (mRequestOrder.cashReceiptType) {
                                    RequestOrder.CashReceiptType.MOBILE.code -> "$mRecipientPhone1$mRecipientPhone2$mRecipientPhone3"
                                    RequestOrder.CashReceiptType.BUSINESS.code -> "$mRecipientCorporation1$mRecipientCorporation2$mRecipientCorporation3"
                                    else -> ""
                                }
                            }

                            if (mRequestOrder.cartItemPayments.size < cartIdList.size) {
                                mRequestOrder.shippingAddress = this@PaymentViewModel.selectedShippingAddress!!

                                for (item in cartIdList) {
                                    RequestOrder.CartItemPayment().apply {
                                        this.cartItemId = item.toLong()
                                        this.couponNumber = getCouponNumberByCartItemId(item.toLong())
                                    }.let {
                                        mRequestOrder.cartItemPayments.add(it)
                                    }
                                }
                            }

                            // 배송 메세지
                            val defaultShippingMessage = BaseApplication.getInstance().getString(R.string.payment_hint_shippingmemo)
                            if (mRequestOrder.shippingAddress.shippingMessage == defaultShippingMessage)
                                mRequestOrder.shippingAddress.shippingMessage = ""

                            if (selectedShippingMessage.get()?.message == shippingMessages[shippingMessages.size - 2].message) { // 배송메세지 직접 입력
                                selectedShippingMessage = ObservableField(ShippingMessage().apply { this.message = shippingMessage })
                            } else if (selectedShippingMessage.get()?.message ?: defaultShippingMessage == defaultShippingMessage) {
                                selectedShippingMessage = ObservableField(ShippingMessage().apply { this.message = "" })
                            }
                            mRequestOrder.shippingAddress.shippingMessage = selectedShippingMessage.get()?.message?:""

                            // 사용 포인트
                            mRequestOrder.consumptionPoint = usedPointNumber.toInt()

                            val accessToken = Preferences.getToken().accessToken
                            if (this@PaymentViewModel.selectedShippingAddress?.addList == true) {
                                // 배송지 추가
                                if (accessToken != null) {
                                    val userId = JWT(accessToken).getClaim("userId").asInt()
                                    if (userId != null) saveShippingAddress(accessToken, userId)
                                } else {
                                    // [임시] 토큰 없는 경우
                                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_expiretoken))
                                }
                            } else {
                                requestOrder("Bearer $accessToken", mRequestOrder)
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

    private fun checkValidation() {
        Preferences.getToken().accessToken.let { token ->
            if (!token.isNullOrEmpty()) {
                val userId = JWT(token).getClaim("userId").asInt()
                if (userId != null && userId > 0)
                    UserServer.getUserById(OnServerListener { success, o ->
                        if (success && (o as BaseModel<*>).resultCode == ResultCode.SUCCESS.flag) {
                            val user = o.data as User
                            this.mEmailVerification.set(user.emailVerify)

                            val diCode = user.userDetail.diCode
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

    private fun getCouponNumberByCartItemId(cartItemId: Long): String {
        for (item in order.orderItemList)
            if (item.cartItemId == cartItemId) {
                var couponNumber = mSelectedCouponMap[item.dealId]?.couponNumber ?: ""
                couponNumber = if (couponNumber == CouponSelectDialogActivity.CouponFlag().NOT_SELECT_COUPON_NUMBER) "" else couponNumber
                return couponNumber
            }

        return ""
    }

    // 본인인증
    fun onClickVerify() = mVerifyTask()

    private fun addShippingAddress(accessToken: String?) {
        if (this@PaymentViewModel.selectedShippingAddress?.addList == true) {
            // 배송지 추가
            if (accessToken != null) {
                val userId = JWT(accessToken).getClaim("userId").asInt()
                if (userId != null) saveShippingAddress(accessToken, userId)
            } else {
                // [임시] 토큰 없는 경우
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_expiretoken))
            }
        }
    }


    fun onClickChangeShippingAddress() {
        listener.redirectShippingAddressActivity()
    }

    fun onTermsChecked(checked: Boolean) {
        this.termsChecked = ObservableBoolean(checked)
        notifyPropertyChanged(BR.termsChecked)
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