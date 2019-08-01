package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.shippingaddress.ShippingAddress
import io.temco.guhada.data.model.payment.Payment
import java.io.Serializable

/**
 * 주문 정보 클래스
 * 주문 완료 화면에서 사용
 * @author Hyeyeon Park
 */
class PurchaseOrderResponse : Serializable{
    var defaultAddress = false
    var orderList: MutableList<PurchaseOrder> = ArrayList()
    var orderNumber: Long = 0
    var payment: Payment = Payment()
    var pgTid = ""
    var shippingAddress: ShippingAddress = ShippingAddress()
    var totalDiscountDiffPrice = 0
    var totalPaymentPrice = 0
    var totalProdPrice = 0
    var totalShipPrice = 0

    fun getOrderTimestamp() : Long {
        return if(orderList.isNotEmpty())
            orderList[0].orderTimestamp
        else
            0
    }
}