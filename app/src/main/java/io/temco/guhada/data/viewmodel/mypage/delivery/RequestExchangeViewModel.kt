package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ShippingMessageCode
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
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
    var mPurchaseOrder: MutableLiveData<PurchaseOrder> = MutableLiveData()
    var mExchangeRequest = ExchangeRequest()
    var mSeller: MutableLiveData<Seller> = MutableLiveData()
    var mSellerAddress: MutableLiveData<SellerAddress> = MutableLiveData()
    var mShippingMessageList: MutableLiveData<MutableList<ShippingMessage>> = MutableLiveData(mutableListOf())
    var mShippingCompanyList: MutableLiveData<MutableList<ShippingCompany>> = MutableLiveData(mutableListOf())
    var mSuccessRequestExchangeTask: (purchaseOrder: PurchaseOrder) -> Unit = {}
    var mSuccessUpdateExchangeTask: () -> Unit = {}
    var mShippingPayment: Int = ShippingPaymentType.BOX.pos
    var mOrderProdGroupId = 0L
    var mOrderClaimId = 0L
    var mSelectedShippingPayment: OrderChangeCause? = null

    // 교환 신청 call 여부
    var mIsExchangeCallFinished = false

    fun getClaimForm(orderProdGroupId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getClaimForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (it.data != null) {
                                (it.data as PurchaseOrder).orderProdGroupId = mOrderProdGroupId
                                (it.data as PurchaseOrder).receiverMessage = ""
                                this@RequestExchangeViewModel.mPurchaseOrder.postValue(it.data as PurchaseOrder)

                                val list = (it.data as PurchaseOrder).shippingMessageList
                                mShippingMessageList.postValue(list)
                            }
                        })
            }, accessToken = token, orderProdGroupId = orderProdGroupId)
        })
    }

    fun getUpdateClaimForm(orderClaimId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getUpdateClaimForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (it.data != null) {
                                val purchaseOrder = (it.data as PurchaseOrder)
                                purchaseOrder.orderClaimId = mOrderClaimId
                                // purchaseOrder.orderProdGroupId = mOrderProdGroupId
                                this.mPurchaseOrder.postValue(purchaseOrder)

                                mExchangeRequest.claimShippingPriceType = purchaseOrder.exchangeShippingPriceType

                                // shipping address
                                mExchangeRequest.exchangeShippingAddress.recipientName = purchaseOrder.receiverName
                                        ?: ""
                                mExchangeRequest.exchangeShippingAddress.recipientMobile = purchaseOrder.exchangeBuyerRecipientMobile
                                        ?: ""
                                mExchangeRequest.exchangeShippingAddress.zip = purchaseOrder.exchangeBuyerZip
                                        ?: ""
                                mExchangeRequest.exchangeShippingAddress.address = purchaseOrder.exchangeBuyerAddress
                                        ?: ""
                                mExchangeRequest.exchangeShippingAddress.roadAddress = purchaseOrder.exchangeBuyerRoadAddress
                                        ?: ""
                                mExchangeRequest.exchangeShippingAddress.detailAddress = purchaseOrder.exchangeBuyerDetailAddress
                                        ?: ""
                                mExchangeRequest.exchangeShippingAddress.shippingMessage = purchaseOrder.exchangeBuyerShippingMessage
                                        ?: ""

                                val list = purchaseOrder.shippingMessageList
                                list.add(ShippingMessage().apply {
                                    message = BaseApplication.getInstance().getString(R.string.shippingmemo_self)
                                })
                                mShippingMessageList.postValue(list)
                            }
                        })
            }, accessToken = token, orderClaimId = orderClaimId)
        })
    }

    fun getSellerInfo() {
        if (mPurchaseOrder.value?.sellerId ?: 0 > 0) {
            UserServer.getSellerById(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val seller = it.data as Seller
                            mSeller.postValue(seller)
                        })
            }, sellerId = mPurchaseOrder.value?.sellerId?.toLong() ?: 0)
        }
    }

    fun getSellerDefaultReturnAddress() {
        if (mPurchaseOrder.value?.sellerId ?: 0 > 0) {
            UserServer.getSellerDefaultReturnAddress(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val sellerAddress = it.data as SellerAddress
                            mSellerAddress.postValue(sellerAddress)
                        })
            }, sellerId = mPurchaseOrder.value?.sellerId?.toLong() ?: 0)
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
        if (mExchangeRequest.exchangeReason.isNullOrEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_exchange_cause))
        } else if (mExchangeRequest.exchangeReasonDetail.isNullOrEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_exchange_hint_cause))
        } else if (mExchangeRequest.alreadySend == null) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_exchange_way_message1))
        } else if (mExchangeRequest.alreadySend == true && mExchangeRequest.shippingCompanyCode.isNullOrEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_exchange_way_message2))
        } else if (mSelectedShippingPayment == null || (mSelectedShippingPayment?.userFault == true && mExchangeRequest.claimShippingPriceType == ShippingPaymentType.NONE.type)) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_exchange_shipping))
        } else {
            if (!mIsExchangeCallFinished) {
                mIsExchangeCallFinished = true
                ServerCallbackUtil.callWithToken(task = { accessToken ->
                    ClaimServer.requestExchange(OnServerListener { success, o ->
                        mIsExchangeCallFinished = false

                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    val result = it.data as PurchaseOrder
                                    mPurchaseOrder.value?.paymentMethodText = result.paymentMethodText
                                    mPurchaseOrder.value?.orderStatusText = result.orderStatusText
                                    mPurchaseOrder.value?.claimStatusText = result.claimStatusText
                                    mSuccessRequestExchangeTask(mPurchaseOrder.value!!)
                                }, failedTask = {
                            ToastUtil.showMessage("[${it.resultCode}] ${BaseApplication.getInstance().getString(R.string.common_message_servererror)}")
                        }, dataIsNull = {
                            if (it is BaseModel<*>) {
                                CommonUtil.debug(it.message)
                                ToastUtil.showMessage(it.message)
                            } else {
                                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                            }
                        })
                    }, accessToken = accessToken, exchangeRequest = mExchangeRequest)
                })
            }
        }
    }

    fun updateExchange() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.updateExchange(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mSuccessUpdateExchangeTask()
                        }, failedTask = {
                    ToastUtil.showMessage("[${it.resultCode}] ${BaseApplication.getInstance().getString(R.string.common_message_servererror)}")
                }, dataIsNull = {
                    if (it is BaseModel<*>) {
                        CommonUtil.debug(it.message)
                        ToastUtil.showMessage(it.message)
                    } else {
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                    }
                })
            }, accessToken = accessToken, exchangeRequest = mExchangeRequest)
        })
    }
}