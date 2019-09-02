package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.RefundRequest
import io.temco.guhada.data.model.ShippingCompany
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerAddress
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class RequestRefundViewModel : BaseObservableViewModel() {
    var mPurchaseOrder = MutableLiveData<PurchaseOrder>()
    var mRefundRequest = RefundRequest()
    var mSellerAddress: MutableLiveData<SellerAddress> = MutableLiveData()
    var mSeller: MutableLiveData<Seller> = MutableLiveData()
    var mShippingCompanyList: MutableLiveData<MutableList<ShippingCompany>> = MutableLiveData(mutableListOf())
    var mShippingPayment: Int = ShippingPaymentType.NONE.pos
    var mSuccessRequestRefundTask: (purchaseOrder: PurchaseOrder) -> Unit = {}
    var mSuccessUpdateRefundTask: () -> Unit = {}
    var mCause = ""
    var mOrderProdGroupId = 0L
    var mOrderClaimId = 0L

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
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.requestRefund(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val result = it.data as PurchaseOrder
                            mPurchaseOrder.value?.paymentMethodText = result.paymentMethodText
                            mPurchaseOrder.value?.orderStatusText = result.orderStatusText
                            mPurchaseOrder.value?.claimStatusText = result.claimStatusText
                            mSuccessRequestRefundTask(mPurchaseOrder.value!!)
                        })
            }, accessToken = accessToken, refundRequest = mRefundRequest)
        })
    }

    fun updateRefund() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.updateRefund(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mSuccessUpdateRefundTask()
                        })
            }, accessToken = accessToken, refundRequest = mRefundRequest)
        })
    }
}