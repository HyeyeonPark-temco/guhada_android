package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.order.OrderHistoryResponse
import io.temco.guhada.data.model.order.OrderStatus
import io.temco.guhada.data.model.order.PurchaseOrderResponse
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import java.util.*

/**
 * 19.07.22
 * @author park jungho
 *
 * 주문배송
- 주문 배송 화면으로 제품정보 이미지를 누르면 제품 상세 화면으로 이동 ( 이건 새로운 activity 로 떠서 위에 열림, 햄버거 아이콘은 빼도 된다고 이야기 했음)
- 주문목록에서 주문내역상세로 이동 새로운 화면으로 처리
- 주문배송 화면에서 취소교환환불 버튼을 누르면 별도의 팝업 또는 전체 화면이 떠서 정보를 입력하고 확인 하면 리스트 갱신
- Api 연동은 서버에서 리뷰를 위한 개발을 해서 자세한 연동은 말고 화면개발 먼저
- 웹 프론트 작업 된 내용 공유
○ 주문배송 목록 // /order/my-orders-status (취소, 교환, 반품을 제외한 내역)
○ 주문 취소 // /order-claim/order-cancel
○ 배송지 변경 // /order/order-update/shipping-address
○ 문의하기 // /users/my-page/inquiries
○ 주문자, 배송지, 결제 // /order/order-complete/{purchaseId} (한주문에 전체를 가져오기때문에 목록에서 필요한 값을 뽑아서 써야함)
○ 환불금액 정보는 데이터가 없어서 현재 구현 못함
○ 주문상태 //  /order/my-orders-status (전체)
○ 기간조회 // /order/my-orders/{startDt}/{endDt}/{page}
 *
 */
class MyPageDeliveryViewModel(val context: Context) : BaseObservableViewModel() {
    var startDate: Long = 0 // yyyy.MM.dd
    var endDate: Long = 0 // yyyy.MM.dd
    var page = 1
    var orderHistoryList: MutableLiveData<OrderHistoryResponse> = MutableLiveData()
    var orderStatus: OrderStatus = OrderStatus()
        @Bindable
        get() = field

    fun getOrders() {
        ServerCallbackUtil.callWithToken(
                task = {
                    if (startDate > 0 && endDate > 0) {
                        ServerCallbackUtil.callWithToken(task = {
                            OrderServer.getOrders(OnServerListener { success, o ->
                                ServerCallbackUtil.executeByResultCode(success, o,
                                        successTask = { model ->
                                            val response = (model.data as OrderHistoryResponse)
                                            if (response.page > 1) {
                                                orderHistoryList.value?.page = response.page
                                                orderHistoryList.value?.count = response.count
                                                orderHistoryList.value?.totalPage = response.totalPage
                                                orderHistoryList.value?.orderItemList?.addAll(response.orderItemList)
                                                this.orderHistoryList.postValue(this.orderHistoryList.value)
                                            } else {
                                                this.orderHistoryList.postValue(response)
                                            }
                                        })
                            }, accessToken = it, startDate = startDate, endDate = endDate, page = page++)
                        })
                    }
                },
                invalidTokenTask = {
                    orderHistoryList.postValue(OrderHistoryResponse())
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                })
    }

    fun getOrderStatus() {
        ServerCallbackUtil.callWithToken(task = { token ->
            OrderServer.getOrderStatus(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            orderStatus = it.data as OrderStatus
                            notifyPropertyChanged(BR.orderStatus)
                        })
            }, token)
        })
    }

    fun editShippingAddress(purchaseId: Long) {
        ServerCallbackUtil.callWithToken(task = { token ->
            OrderServer.setOrderCompleted(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val shippingAddress = (it.data as PurchaseOrderResponse).shippingAddress
                            UserShipping().apply {
                                this.id = shippingAddress.id.toInt()
                                this.shippingName = shippingAddress.addressName ?: ""
                                this.defaultAddress = shippingAddress.addressDefault
                                this.zip = shippingAddress.zipcode
                                this.recipientName = shippingAddress.receiverName
                                this.recipientMobile = shippingAddress.phone
                                this.address = shippingAddress.addressBasic
                                this.roadAddress = shippingAddress.addressBasic
                                this.detailAddress = shippingAddress.addressDetail
                                this.pId = purchaseId
                            }.let { userShipping ->
                                EventBusHelper.sendEvent(EventBusData(RequestCode.EDIT_SHIPPING_ADDRESS.flag, userShipping))
                            }
                        })
            }, accessToken = token, purchaseId = purchaseId.toDouble())
        })
    }

//    fun setDate(day: Int, callback: (startDate: String, endDate: String) -> Unit) {
//        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
//        endDate = convertDateFormat(calendar, ".")
//        calendar.add(Calendar.DAY_OF_MONTH, -day)
//        startDate = convertDateFormat(calendar, ".")
//
//        callback(startDate, endDate)
//    }

    fun onClickMore() {
        getOrders()
    }

    private fun convertDateFormat(calendar: Calendar, operator: String): String {
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "${calendar.get(Calendar.YEAR)}$operator${if (month < 10) "0$month" else month}$operator${if (day < 10) "0$day" else day}"
    }

    private fun convertDateFormat(date: String): String {
        val dates = date.split(".")
        if (dates.size > 2) return "${dates[0]}-${dates[1]}-${dates[2]}"
        return ""
    }
}