package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerAddress
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import java.util.*

class RequestRefundViewModel : BaseObservableViewModel(), java.util.Observer {
    var mPurchaseOrder = MutableLiveData<PurchaseOrder>()
    var mRefundRequest = RefundRequest()
    var mSellerAddress: MutableLiveData<SellerAddress> = MutableLiveData()
    var mSeller: MutableLiveData<Seller> = MutableLiveData()
    var mShippingCompanyList: MutableLiveData<MutableList<ShippingCompany>> = MutableLiveData(mutableListOf())
    var mExpectedRefundPrice: MutableLiveData<ExpectedRefundPrice> = MutableLiveData()
    var mSelectedShippingPayment: OrderChangeCause? = null
    var mSuccessRequestRefundTask: (purchaseOrder: PurchaseOrder) -> Unit = {}
    var mSuccessUpdateRefundTask: () -> Unit = {}
    var mOrderProdGroupId = 0L
    var mOrderClaimId = 0L
    var mOrderClaimGroupId = 0L
    var mBankAccount: MutableLiveData<BankAccount> = MutableLiveData()
    var mIsCheckAccountAvailable = ObservableBoolean(true)
        @Bindable
        get() = field

    // 반품 신청 call 여부
    private var mIsRefundCallFinished = false

    init {
        this.mRefundRequest.addObserver(this)
    }

    fun getClaimForm(orderProdGroupId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getClaimForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (it.data != null) {
                                (it.data as PurchaseOrder).orderProdGroupId = mOrderProdGroupId
                                this@RequestRefundViewModel.mPurchaseOrder.postValue(it.data as PurchaseOrder)
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
                                mRefundRequest.claimShippingPriceType = purchaseOrder.returnShippingPriceType
                                this@RequestRefundViewModel.mPurchaseOrder.postValue(purchaseOrder)
                            }
                        })
            }, accessToken = token, orderClaimId = orderClaimId)
        })
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

    fun requestRefund() {
        if (mPurchaseOrder.value?.paymentMethod == "Card") {
            callRequestRefund()
        } else {
            if (mBankAccount.value?.result == true) {
                callRequestRefund()
            } else {
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_requiredcheckaccount))
            }
        }
    }

    private fun callRequestRefund() {
        if (mRefundRequest.refundReason.isNullOrEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_cause))
        } else if (mRefundRequest.refundReasonDetail.isNullOrEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_hint_cause))
        } else if (mRefundRequest.alreadySend == null) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_way_message1))
        } else if (mRefundRequest.alreadySend == true && mRefundRequest.shippingCompanyCode.isNullOrEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_way_message2))
        } else if (mPurchaseOrder.value?.returnShipExpense ?: 0 > 0 && (mSelectedShippingPayment == null || (mSelectedShippingPayment?.userFault == true && mRefundRequest.claimShippingPriceType == ShippingPaymentType.NONE.type))) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_shipping))
        } else {
            if (!mIsRefundCallFinished) {
                mIsRefundCallFinished = true
                ServerCallbackUtil.callWithToken(task = { accessToken ->
                    ClaimServer.requestRefund(OnServerListener { success, o ->
                        mIsRefundCallFinished = false

                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    val result = it.data as PurchaseOrder
                                    mPurchaseOrder.value?.paymentMethodText = result.paymentMethodText
                                    mPurchaseOrder.value?.orderStatusText = result.orderStatusText
                                    mPurchaseOrder.value?.claimStatusText = result.claimStatusText
                                    mSuccessRequestRefundTask(mPurchaseOrder.value!!)
                                },
                                failedTask = {
                                    ToastUtil.showMessage("[${it.resultCode}] ${BaseApplication.getInstance().getString(R.string.common_message_servererror)}")
                                },
                                dataIsNull = {
                                    if (it is BaseModel<*>) {
                                        CommonUtil.debug(it.message)
                                        ToastUtil.showMessage(it.message)
                                    } else {
                                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                                    }
                                })
                    }, accessToken = accessToken, refundRequest = mRefundRequest)
                })
            }
        }

    }

    fun updateRefund() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.updateRefund(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mSuccessUpdateRefundTask()
                        }, failedTask = {
                    ToastUtil.showMessage("[${it.resultCode}] ${BaseApplication.getInstance().getString(R.string.common_message_servererror)}")
                }, dataIsNull = {
                    if (it is BaseModel<*>) {
                        CommonUtil.debug(it.message)
                        ToastUtil.showMessage(it.message)
                    } else {
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                    }
                }
                )
            }, accessToken = accessToken, refundRequest = mRefundRequest)
        })
    }

    fun checkAccount() = when {
        mRefundRequest.refundBankCode.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_emptybankcode))
        mRefundRequest.refundBankAccountNumber.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_emptybanknumber))
        else -> BankAccount().apply {
            this.bankNumber = mRefundRequest.refundBankAccountNumber
            this.bankCode = mRefundRequest.refundBankCode
        }.let {
            if (mIsCheckAccountAvailable.get()) {
                OrderServer.checkAccount(OnServerListener { success, o ->
                    if (success) {
                        val bankAccount = (o as BaseModel<BankAccount>).data
                        if (bankAccount.result) {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_succesbankaccount))

                            mRefundRequest.refundBankCode = bankAccount.bankCode
                            mRefundRequest.refundBankAccountNumber = bankAccount.bankNumber
                            mRefundRequest.refundBankAccountOwner = bankAccount.name

                            mIsCheckAccountAvailable = ObservableBoolean(false)
                            notifyPropertyChanged(BR.mIsCheckAccountAvailable)
                            this.mBankAccount.postValue(bankAccount)
                        } else {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_invalidbankaccount))
                        }
                    }
                }, bankAccount = it)
            }
        }
    }

    fun getExpectedRefundPriceForRequest(quantity: Int) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.getExpectedRefundPriceForRequest(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                    mExpectedRefundPrice.postValue(it.data as ExpectedRefundPrice)
                })
            }, accessToken = accessToken, orderProdGroupId = mOrderProdGroupId, quantity = quantity)
        })
    }

    override fun update(o: Observable?, arg: Any?) {
        if (arg is String) {
            if (arg == "bankNumber") {
                mIsCheckAccountAvailable = ObservableBoolean(true)
                notifyPropertyChanged(BR.mIsCheckAccountAvailable)
            }
        }
    }
}