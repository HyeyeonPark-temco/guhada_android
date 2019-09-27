package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.CancelRequest
import io.temco.guhada.data.model.ExpectedRefundPrice
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CancelOrderViewModel : BaseObservableViewModel() {
    var purchaseOrder: MutableLiveData<PurchaseOrder> = MutableLiveData(PurchaseOrder())
    var mOrderProdGroupId = 0L
    var quantity = 1
    var selectedCausePos = -1
    var cause = ""
    var successCancelOrderTask: (result: PurchaseOrder) -> Unit = {}
    var mExpectedRefundPrice = MutableLiveData<ExpectedRefundPrice>()

    fun getClaimForm(orderProdGroupId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getClaimForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (it.data != null)
                                this@CancelOrderViewModel.purchaseOrder.postValue(it.data as PurchaseOrder)
                        },
                        dataIsNull = {
                            if (it is BaseModel<*>) {
                                CommonUtil.debug(it.message)
                                ToastUtil.showMessage(it.message)
                            } else {
                                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                            }
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
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_cancel_hint_cause))
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
            }, accessToken = token, cancelRequest = cancelRequest)
        })
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

}