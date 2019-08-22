package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.CancelRequest
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CancelOrderViewModel : BaseObservableViewModel() {
    var purchaseOrder: MutableLiveData<PurchaseOrder> = MutableLiveData(PurchaseOrder())
    var mOrderProdGroupId = 0L
    var quantity = 1
    var selectedCausePos = -1
    var cause = ""
    var successCancelOrderTask: (result : PurchaseOrder) -> Unit = {}

    fun getClaimForm(orderProdGroupId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getClaimForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if(it.data != null)
                                this@CancelOrderViewModel.purchaseOrder.postValue(it.data as PurchaseOrder)
                        })
            }, accessToken = token, orderProdGroupId = orderProdGroupId)
        })
    }

    fun cancelOrder() {
        val cancelRequest = CancelRequest().apply {
            when {
                selectedCausePos < 0 -> {
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_cancel_cause))
                    return
                }
                cause.isEmpty() -> {
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_cancel_cause))
                    return
                }
                else -> {
                    this.cancelReason = this@CancelOrderViewModel.purchaseOrder.value?.cancelReasonList?.get(selectedCausePos)?.code.toString()
                    this.cancelReasonDetail = this@CancelOrderViewModel.cause
                    this.quantity = this@CancelOrderViewModel.quantity
                    this.orderProdGroupId = this@CancelOrderViewModel.mOrderProdGroupId
                }
            }
        }

        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.cancelOrder(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val result = it.data as PurchaseOrder
                            successCancelOrderTask(result)
                        })
            }, accessToken = token, cancelRequest = cancelRequest)
        })
    }
}