package io.temco.guhada.data.model.order

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.order.OrderItemResponse
import java.io.Serializable

/**
 * 주문 정보 클래스
 * @see PurchaseOrderResponse
 * @author Hyeyeon Park
 */
class PurchaseOrder : OrderItemResponse(), Serializable {
    var purchaseId: Long = 0

    var expireDate = ""
    var expireTimestamp: Long = 0

    var orderDate = intArrayOf() // 2019,5,14
    var orderTimestamp: Long = 0

    var optionAttribute1: String? = ""
    var optionAttribute2: String? = ""
    var optionAttribute3: String? = ""

    var orderPrice = 0
    var orderProdGroupId: Long = 0
    var orderProdId: Long = 0
    var originalPrice = 0

    @SerializedName("prodName")
    var productName = ""

    var purchaseConfirm = false
    var purchaseStatusText = ""

    var shipCompleteDate = "" // 2019-05-10
    var shipPrice = 0

    var statusMessage = "" // 주문 완료 메세지
}
