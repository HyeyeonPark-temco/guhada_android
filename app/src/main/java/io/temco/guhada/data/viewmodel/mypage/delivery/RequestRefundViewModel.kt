package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.RefundCause
import io.temco.guhada.common.enum.ShippingPaymentType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.OrderChangeCause
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
    var mCause = ""
    val mCauseList = mutableListOf(
            OrderChangeCause().apply {
                label = RefundCause.DAMAGED.label
                code = RefundCause.DAMAGED.code
                isFeeCharged = RefundCause.DAMAGED.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.DELIVERY_OMITTED.label
                code = RefundCause.DELIVERY_OMITTED.code
                isFeeCharged = RefundCause.DELIVERY_OMITTED.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.DIFFERENT_OPTION.label
                code = RefundCause.DIFFERENT_OPTION.code
                isFeeCharged = RefundCause.DIFFERENT_OPTION.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.DIFFERENT_PRODUCT.label
                code = RefundCause.DIFFERENT_PRODUCT.code
                isFeeCharged = RefundCause.DIFFERENT_PRODUCT.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.LATE_DELIVERY.label
                code = RefundCause.LATE_DELIVERY.code
                isFeeCharged = RefundCause.LATE_DELIVERY.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.MISTAKE_DELIVERY.label
                code = RefundCause.MISTAKE_DELIVERY.code
                isFeeCharged = RefundCause.MISTAKE_DELIVERY.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.MISTAKE_ORDER.label
                code = RefundCause.MISTAKE_ORDER.code
                isFeeCharged = RefundCause.MISTAKE_ORDER.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.SERVICE_UNSATISFIED.label
                code = RefundCause.SERVICE_UNSATISFIED.code
                isFeeCharged = RefundCause.SERVICE_UNSATISFIED.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.SOLD_OUT.label
                code = RefundCause.SOLD_OUT.code
                isFeeCharged = RefundCause.SOLD_OUT.isFeeCharged
            },
            OrderChangeCause().apply {
                label = RefundCause.ETC.label
                code = RefundCause.ETC.code
                isFeeCharged = RefundCause.ETC.isFeeCharged
            })
    var mOrderProdGroupId = 0L

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
}