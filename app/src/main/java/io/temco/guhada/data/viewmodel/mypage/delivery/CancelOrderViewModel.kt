package io.temco.guhada.data.viewmodel.mypage.delivery

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
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
    var mCloseActivityTask: () -> Unit = {}

    // 취소 신청 api call 여부
    private var mIsCancelCallFinished = false

    fun getClaimForm(orderProdGroupId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.getClaimForm(OnServerListener { success, o ->
                mFailCancelOrderTask()
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
        }, invalidTokenTask = { mCloseActivityTask() })
    }

    fun getExpectedRefundPriceForRequest(quantity: Int) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.getExpectedRefundPriceForRequest(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                    mExpectedRefundPrice.postValue(it.data as ExpectedRefundPrice)
                })
            }, accessToken = accessToken, orderProdGroupId = mOrderProdGroupId, quantity = quantity)
        }, invalidTokenTask = { mCloseActivityTask() })
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
                        mIsCancelCallFinished = false
                        return
                    }
                    mCause.isEmpty() -> {
                        mFailCancelOrderTask()
                        mIsCancelCallFinished = false
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
                    mFailCancelOrderTask()

                    if (success && o is BaseModel<*>) {
                        if (o.resultCode == ResultCode.SUCCESS.flag) {
                            val result = o.data as PurchaseOrder
                            mSuccessCancelOrderTask(result)
                        } else {
                            CommonUtil.debug(o.message)
                            ToastUtil.showMessage(o.message)
                        }
                    } else
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))

                }, accessToken = token, cancelRequest = cancelRequest)
            }, invalidTokenTask = {
                mIsCancelCallFinished = false
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                mFailCancelOrderTask()
                mCloseActivityTask()
            })
        }
    }

}