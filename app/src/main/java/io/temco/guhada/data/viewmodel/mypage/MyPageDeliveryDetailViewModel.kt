package io.temco.guhada.data.viewmodel.mypage

import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.order.PurchaseOrderResponse
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class MyPageDeliveryDetailViewModel : BaseObservableViewModel() {
    var purchaseId: Long = -1
    var mOrderProdGroupId: Long = 0L
    var refundInfoVisible = false
    var purchaseOrderResponse = PurchaseOrderResponse()
        @Bindable
        get() = field
    var user: User = User()
        @Bindable
        get() = field
    var onClickClaimTask: (productId: Long) -> Unit = {}
    var onClickReceiptTask: (tId: String) -> Unit = {}


    ///
    var mPurchaseOrder = ObservableField<PurchaseOrder>(PurchaseOrder())
        @Bindable
        get() = field

    fun getClaimForm() {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getClaimForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (it.data != null) {
                                (it.data as PurchaseOrder).orderProdGroupId = mOrderProdGroupId
                                this@MyPageDeliveryDetailViewModel.mPurchaseOrder = ObservableField(it.data as PurchaseOrder)
                                notifyPropertyChanged(BR.mPurchaseOrder)
                                //  this@MyPageDeliveryDetailViewModel.mPurchaseOrder.postValue(it.data as PurchaseOrder)
                            }
                        })
            }, accessToken = token, orderProdGroupId = mOrderProdGroupId)
        })
    }

    fun getOrder() {
        ServerCallbackUtil.callWithToken(task = { token ->
            OrderServer.setOrderCompleted(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val data = it.data as PurchaseOrderResponse
                            this.purchaseOrderResponse = data
                            notifyPropertyChanged(BR.purchaseOrderResponse)
                        })
            }, accessToken = token, purchaseId = purchaseId.toDouble())
        })
    }

    fun getUser() {
        ServerCallbackUtil.callWithToken(task = { token ->
            val userId = JWT(token.split("Bearer ")[1]).getClaim("userId").asInt()
            if (userId != null)
                UserServer.getUserInfo(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                val data = it.list[0] as User
                                this.user = data
                                notifyPropertyChanged(BR.user)
                            })
                }, userId)
            else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
        })
    }

    fun onClickClaim() = onClickClaimTask(purchaseOrderResponse.orderList[0].productId)

    fun onClickReceipt() = onClickReceiptTask(purchaseOrderResponse.pgTid)
}