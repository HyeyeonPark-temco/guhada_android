package io.temco.guhada.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 배송지 정보 클래스
 * @see io.temco.guhada.data.model.order.Order
 * @see io.temco.guhada.data.model.order.RequestOrder
 * @author Hyeyeon park
 */
class UserShipping : Serializable {
    var id: Int = -1
    var userId: Int = -1
    var defaultAddress = false
    var safetyMobile = true
    var shippingName = ""
    var shippingMessage = ""
    var address = ""
    var zip = ""
    var roadAddress = ""
    var detailAddress = ""
    var addShippingAddress = false

    @SerializedName(value = "recipientName", alternate = ["recepientName"])
    var recipientName = ""

    @SerializedName(value = "recipientMobile", alternate = ["recepientMobile"])
    var recipientMobile = ""

    @Expose
    var addList = false // 배송지 목록에 추가 여부
        set(value) {
            field = value
            addShippingAddress = value
        }

    @Expose
    var pId: Long = 0 // purchaseId
}
