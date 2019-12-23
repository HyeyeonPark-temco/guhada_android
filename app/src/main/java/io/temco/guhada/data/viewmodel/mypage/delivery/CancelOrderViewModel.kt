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
    var mOrderProdGroupId = 0L
    var mQuantity = 1
    var mSelectedCausePos = -1
    var mCause = ""

    var mPurchaseOrder: MutableLiveData<PurchaseOrder> = MutableLiveData(PurchaseOrder())
    var mExpectedRefundPrice = MutableLiveData<ExpectedRefundPrice>()
    var mExpectedRefundInfo = MutableLiveData<ExpectedRefundPrice.ExpectedRefundInfo>()

    var mShowIndicatorTask: () -> Unit = {}
    var mSuccessCancelOrderTask: (result: PurchaseOrder) -> Unit = {}
    var mFailCancelOrderTask: () -> Unit = {}

    // 취소 신청 api call 여부
    private var mIsCancelCallFinished = false

    fun getClaimForm(orderProdGroupId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getClaimForm(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (it.data != null)
                                this@CancelOrderViewModel.mPurchaseOrder.postValue(it.data as PurchaseOrder)
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

    fun getExpectedRefundPriceForRequest(quantity: Int) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.getExpectedRefundPriceForRequest(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                    mExpectedRefundPrice.postValue(it.data as ExpectedRefundPrice)
                })
            }, accessToken = accessToken, orderProdGroupId = mOrderProdGroupId, quantity = quantity)
        })
    }

    fun cancelOrder() {
        if (!mIsCancelCallFinished) {
            mShowIndicatorTask()
            mIsCancelCallFinished = true

            val cancelRequest = CancelRequest().apply {
                when {
                    mSelectedCausePos < 0 -> {
                        mFailCancelOrderTask()
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_cancel_cause))
                        return
                    }
                    mCause.isEmpty() -> {
                        mFailCancelOrderTask()
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_cancel_hint_cause))
                        return
                    }
                    else -> {
                        this.cancelReason = this@CancelOrderViewModel.mPurchaseOrder.value?.cancelReasonList?.get(mSelectedCausePos)?.code.toString()
                        this.cancelReasonDetail = this@CancelOrderViewModel.mCause
                        this.quantity = this@CancelOrderViewModel.mQuantity
                        this.orderProdGroupId = this@CancelOrderViewModel.mOrderProdGroupId
                    }
                }
            }

            ServerCallbackUtil.callWithToken(task = { token ->
                ClaimServer.cancelOrder(OnServerListener { success, o ->
                    mIsCancelCallFinished = false
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                val result = it.data as PurchaseOrder
                                mSuccessCancelOrderTask(result)
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
            }, invalidTokenTask = {
                mIsCancelCallFinished = false
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            })
        }
    }

}