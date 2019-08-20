package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ExchangeCause
import io.temco.guhada.common.enum.ShippingMessageCode
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.ExchangeRequest
import io.temco.guhada.data.model.OrderChangeCause
import io.temco.guhada.data.model.ShippingCompany
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.order.Order
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerAddress
import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class RequestExchangeViewModel : BaseObservableViewModel() {
//    enum class ShippingPayment(val flag: Int) {
//        EXCLUDE_REFUND_PRICE(0), // 환불 금액에서 차감
//        INSIDE_BOX(1), // 박스에 동봉
//        SEND_DIRECTLY(2) // 판매자에게 직접 송금
//    }

    var mPurchaseOrder = PurchaseOrder()
    var mExchangeRequest = ExchangeRequest()
    var mSeller: MutableLiveData<Seller> = MutableLiveData()
    var mSellerAddress: MutableLiveData<SellerAddress> = MutableLiveData()
    var mShippingMessageList: MutableLiveData<MutableList<ShippingMessage>> = MutableLiveData(mutableListOf())
    var mShippingCompanyList: MutableLiveData<MutableList<ShippingCompany>> = MutableLiveData(mutableListOf())
    var mSuccessRequestExchangeTask: (purchaseOrder: PurchaseOrder) -> Unit = {}
    var mShippingPayment: Int = ShippingPaymentType.BOX.pos
    val mCauseList = mutableListOf(
            OrderChangeCause().apply {
                label = ExchangeCause.COLOR_SIZE_CHANGE.label
                code = ExchangeCause.COLOR_SIZE_CHANGE.code
                isFeeCharged = ExchangeCause.COLOR_SIZE_CHANGE.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.DAMAGED.label
                code = ExchangeCause.DAMAGED.code
                isFeeCharged = ExchangeCause.DAMAGED.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.DELIVERY_OMITTED.label
                code = ExchangeCause.DELIVERY_OMITTED.code
                isFeeCharged = ExchangeCause.DELIVERY_OMITTED.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.DIFFERENT_OPTION.label
                code = ExchangeCause.DIFFERENT_OPTION.code
                isFeeCharged = ExchangeCause.DIFFERENT_OPTION.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.DIFFERENT_PRODUCT.label
                code = ExchangeCause.DIFFERENT_PRODUCT.code
                isFeeCharged = ExchangeCause.DIFFERENT_PRODUCT.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.LATE_DELIVERY.label
                code = ExchangeCause.LATE_DELIVERY.code
                isFeeCharged = ExchangeCause.LATE_DELIVERY.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.MISTAKE_DELIVERY.label
                code = ExchangeCause.MISTAKE_DELIVERY.code
                isFeeCharged = ExchangeCause.MISTAKE_DELIVERY.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.MISTAKE_ORDER.label
                code = ExchangeCause.MISTAKE_ORDER.code
                isFeeCharged = ExchangeCause.MISTAKE_ORDER.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.SERVICE_UNSATISFIED.label
                code = ExchangeCause.SERVICE_UNSATISFIED.code
                isFeeCharged = ExchangeCause.SERVICE_UNSATISFIED.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.SOLD_OUT.label
                code = ExchangeCause.SOLD_OUT.code
                isFeeCharged = ExchangeCause.SOLD_OUT.isFeeCharged
            },
            OrderChangeCause().apply {
                label = ExchangeCause.ETC.label
                code = ExchangeCause.ETC.code
                isFeeCharged = ExchangeCause.ETC.isFeeCharged
            })

    fun getSellerInfo() {
        if (mPurchaseOrder.sellerId > 0) {
            UserServer.getSellerById(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val seller = it.data as Seller
                            mSeller.postValue(seller)
                        })
            }, sellerId = mPurchaseOrder.sellerId.toLong())
        }
    }

    fun getSellerDefaultReturnAddress() {
        if (mPurchaseOrder.sellerId > 0) {
            UserServer.getSellerDefaultReturnAddress(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val sellerAddress = it.data as SellerAddress
                            mSellerAddress.postValue(sellerAddress)
                        })
            }, mPurchaseOrder.sellerId.toLong())
        }
    }

    fun getShippingMessageByOrderForm() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            OrderServer.getOrderForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val order = (o as BaseModel<*>).data as Order
                            val application = BaseApplication.getInstance()
                            Observable.fromIterable(order.shippingMessage)
                                    .map {
                                        when (it.message) {
                                            application.getString(R.string.shippingmemo_before_call) -> it.type = ShippingMessageCode.BEFORE_CALL.code
                                            application.getString(R.string.shippingmemo_security) -> it.type = ShippingMessageCode.SECURITY.code
                                            application.getString(R.string.shippingmemo_call_if_absent) -> it.type = ShippingMessageCode.CALL_IF_ABSENT.code
                                            application.getString(R.string.shippingmemo_put_door) -> it.type = ShippingMessageCode.PUT_DOOR.code
                                            application.getString(R.string.shippingmemo_postbox) -> it.type = ShippingMessageCode.POSTBOX.code
                                            application.getString(R.string.shippingmemo_self) -> it.type = ShippingMessageCode.SELF.code
                                        }
                                        it
                                    }.subscribe {
                                        mShippingMessageList.value?.add(it)
                                    }

                            if (order.shippingAddress != null) mExchangeRequest.exchangeShippingAddress = order.shippingAddress!!

                            mShippingMessageList.value?.add(ShippingMessage().apply {
                                message = application.getString(R.string.shippingmemo_self)
                            })
                            mShippingMessageList.postValue(mShippingMessageList.value)
                        })
            }, accessToken = accessToken, cartIdList = intArrayOf(0))
        })
    }

    fun getShippingCompany() {
        ProductServer.getShippingCompanyList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val companyList = it.data as MutableList<ShippingCompany>
                        companyList.add(ShippingCompany().apply { this.name = BaseApplication.getInstance().getString(R.string.requestorderstatus_common_courier_hint1) })
                        mShippingCompanyList.postValue(companyList)
                    })
        }, type = ShippingCompany.Type.DOMESTIC.type)
    }

    fun requestExchange() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.requestExchange(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mSuccessRequestExchangeTask(mPurchaseOrder)
                        })
            }, accessToken = accessToken, exchangeRequest = mExchangeRequest)
        })
    }
}