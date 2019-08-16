package io.temco.guhada.data.viewmodel

import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ConfirmPurchaseViewModel : BaseObservableViewModel() {
    var purchaseOrder = PurchaseOrder()
    var closeActivityTask: () -> Unit = {}
    var successConfirmPurchaseTask: () -> Unit = {}

    fun onClickCancel() = closeActivityTask()

    fun onClickConfirmPurchase() {
        ServerCallbackUtil.callWithToken(task = {
            OrderServer.confirmPurchase(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { model ->
                            val result = (model.data as Boolean)
                            if (result) successConfirmPurchaseTask()
                        })
            }, accessToken = it, orderProdGroupId = purchaseOrder.orderProdGroupId)
        })
    }

}