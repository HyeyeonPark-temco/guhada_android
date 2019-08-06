package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.order.CancelOrderStatus
import io.temco.guhada.data.model.order.OrderHistoryResponse
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * created 19.07.22
 * @author park jungho
 *
 * @author Hyeyeon Park
 * @since 2019.08.05
 *
 * 취소교환환불
- 해당 탭의 제품 요청, 처리중, 완료 리스트
- 제플린에 신청 입력 화면과 같은건 일단 무시하고 리스트로만 보이도록
- 웹 프론트 작업 된 내용 공유
○ 주문 취소 신청  // /order-claim/order-cancel
○ 환불금액 정보는 데이터가 없어서 현재 구현 못함
 *
 */
class MyPageDeliveryCerViewModel(val context: Context) : BaseObservableViewModel() {
    var page = 1
    var startDate: Long = 0
    var endDate: Long = 0
    var cancelOrderStatus: MutableLiveData<CancelOrderStatus> = MutableLiveData()
    var cancelOrderHistory: MutableLiveData<OrderHistoryResponse> = MutableLiveData()

    fun getCancelOrderStatus() {
        if (startDate > 0 && endDate > 0) {
            ServerCallbackUtil.callWithToken(task = { token ->
                ClaimServer.getCancelOrderStatus(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                //                                this.cancelOrderStatus.apply { it.data as CancelOrderStatus }
                                this.cancelOrderStatus.postValue(it.data as CancelOrderStatus)
                            })
                }, accessToken = token, startTimeStamp = startDate, endTimeStamp = endDate)
            })
        }
    }

    fun getCancelOrderHistories() {
        Log.e("취소내역", "start: $startDate, end: $endDate")
        if (startDate > 0 && endDate > 0) {
            ServerCallbackUtil.callWithToken(task = { token ->
                ClaimServer.getCancelOrders(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                this.cancelOrderHistory.postValue(it.data as OrderHistoryResponse)
                            })
                }, accessToken = token, startTimeStamp = startDate, endTimeStamp = endDate, page = page++)
            })
        }
    }

    fun onClickMore() {

    }

}