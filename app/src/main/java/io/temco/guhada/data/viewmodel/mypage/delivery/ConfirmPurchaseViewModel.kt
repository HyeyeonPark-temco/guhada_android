package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.point.ExpectedPointResponse
import io.temco.guhada.data.model.point.PointPopupInfo
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ConfirmPurchaseViewModel : BaseObservableViewModel() {
    var purchaseOrder = PurchaseOrder()
    var closeActivityTask: () -> Unit = {}
    var successConfirmPurchaseTask: (pointInfo: PointPopupInfo) -> Unit = {}
    var mExpectedPointResponse: MutableLiveData<ExpectedPointResponse> = MutableLiveData()

    fun getConfirmProductDueSavePoint() {
        ServerCallbackUtil.callWithToken(task = {
            BenefitServer.getConfirmProductDueSavePoint(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { model ->
                            mExpectedPointResponse.postValue(model.data as ExpectedPointResponse)
                        })
            }, accessToken = it, orderProdGroupId = purchaseOrder.orderProdGroupId)
        })
    }

    fun onClickCancel() = closeActivityTask()

    fun onClickConfirmPurchase() {
        ServerCallbackUtil.callWithToken(task = {
            OrderServer.confirmPurchase(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { model ->
                            if (model.data is PointPopupInfo)
                                successConfirmPurchaseTask(model.data as PointPopupInfo)
                        })
            }, accessToken = it, orderProdGroupId = purchaseOrder.orderProdGroupId)
        })
    }

}