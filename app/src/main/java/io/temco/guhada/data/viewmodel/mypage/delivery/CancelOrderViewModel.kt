package io.temco.guhada.data.viewmodel.mypage.delivery

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.CancelOrderCause
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.CancelRequest
import io.temco.guhada.data.model.OrderChangeCause
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CancelOrderViewModel : BaseObservableViewModel() {
    var purchaseOrder: PurchaseOrder = PurchaseOrder()
    var quantity = 1
    var selectedCausePos = -1
    var cause = ""
    var causes = mutableListOf(
            OrderChangeCause().apply {
                this.code = CancelOrderCause.CHANGE_MIND.code
                this.label = CancelOrderCause.CHANGE_MIND.label
                this.isFeeCharged = CancelOrderCause.CHANGE_MIND.isFeeCharged
            },
            OrderChangeCause().apply {
                this.code = CancelOrderCause.COLOR_SIZE_CHANGE.code
                this.label = CancelOrderCause.COLOR_SIZE_CHANGE.label
                this.isFeeCharged = CancelOrderCause.COLOR_SIZE_CHANGE.isFeeCharged
            },
            OrderChangeCause().apply {
                this.code = CancelOrderCause.MISTAKE_ORDER.code
                this.label = CancelOrderCause.MISTAKE_ORDER.label
                this.isFeeCharged = CancelOrderCause.MISTAKE_ORDER.isFeeCharged
            },
            OrderChangeCause().apply {
                this.code = CancelOrderCause.LATE_DELIVERY.code
                this.label = CancelOrderCause.LATE_DELIVERY.label
                this.isFeeCharged = CancelOrderCause.LATE_DELIVERY.isFeeCharged
            },
            OrderChangeCause().apply {
                this.code = CancelOrderCause.SOLD_OUT.code
                this.label = CancelOrderCause.SOLD_OUT.label
                this.isFeeCharged = CancelOrderCause.SOLD_OUT.isFeeCharged
            },
            OrderChangeCause().apply {
                this.code = CancelOrderCause.SERVICE_UNSATISFIED.code
                this.label = CancelOrderCause.SERVICE_UNSATISFIED.label
                this.isFeeCharged = CancelOrderCause.SERVICE_UNSATISFIED.isFeeCharged
            },
            OrderChangeCause().apply {
                this.code = CancelOrderCause.DIFFERENT_PRODUCT.code
                this.label = CancelOrderCause.DIFFERENT_PRODUCT.label
                this.isFeeCharged = CancelOrderCause.DIFFERENT_PRODUCT.isFeeCharged
            })
    var successCancelOrderTask: () -> Unit = {}

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
                    this.cancelReason = this@CancelOrderViewModel.causes[selectedCausePos].code
                    this.cancelReasonDetail = this@CancelOrderViewModel.cause
                    this.quantity = this@CancelOrderViewModel.quantity
                    this.orderProdGroupId = this@CancelOrderViewModel.purchaseOrder.orderProdGroupId
                }
            }
        }

        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.cancelOrder(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val result = it.data as Boolean
                            if (result) successCancelOrderTask()
                            else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_error))
                        })
            }, accessToken = token, cancelRequest = cancelRequest)
        })

    }
}