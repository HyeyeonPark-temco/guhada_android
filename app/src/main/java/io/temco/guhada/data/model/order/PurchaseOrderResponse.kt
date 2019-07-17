package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.ShippingAddress
import io.temco.guhada.data.model.payment.Payment
import java.io.Serializable

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
}