package io.temco.guhada.data.model.order

import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.shippingaddress.ShippingAddress
import io.temco.guhada.data.model.payment.Payment
import java.io.Serializable

/**
 * 주문 정보 클래스
 * 주문 완료 화면에서 사용
 * @author Hyeyeon Park
 */
class PurchaseOrderResponse : Serializable {
    var defaultAddress = false
    var orderList: MutableList<PurchaseOrder> = ArrayList()
    var orderNumber: Long = 0
    var payment: Payment = Payment()
    var pgTid = ""
    var shippingAddress: ShippingAddress = ShippingAddress()
    var totalPaymentPrice = 0
    var totalProdPrice = 0
    var totalShipPrice = 0
    var etcMessage = ""     // 기타 요청 사항

    var buyerName = ""
    var buyerEmail = ""
    var buyerPhone = ""

    // 할인 내역 구성
    var couponPointProdDiscountPrice = 0
    var couponDiscountPrice = 0
    var totalDiscountDiffPrice = 0
    var totalPointPayment = 0
    var totalAmount = 0

    fun getOrderTimestamp(): Long {
        return if (orderList.isNotEmpty())
            orderList[0].orderTimestamp
        else
            0
    }

    override fun toString(): String {
        if(CustomLog.flag)return "PurchaseOrderResponse(defaultAddress=$defaultAddress, orderList=$orderList, orderNumber=$orderNumber, payment=$payment, pgTid='$pgTid', shippingAddress=$shippingAddress, totalPaymentPrice=$totalPaymentPrice, totalProdPrice=$totalProdPrice, totalShipPrice=$totalShipPrice, buyerName='$buyerName', buyerEmail='$buyerEmail', buyerPhone='$buyerPhone', couponPointProdDiscountPrice=$couponPointProdDiscountPrice, couponDiscountPrice=$couponDiscountPrice, totalDiscountDiffPrice=$totalDiscountDiffPrice, totalPointPayment=$totalPointPayment, totalAmount=$totalAmount)"
        else return ""
    }


}