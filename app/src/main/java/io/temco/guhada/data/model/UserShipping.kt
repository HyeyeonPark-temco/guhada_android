package io.temco.guhada.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserShipping : Serializable {
    var id: Int = -1
    var userId: Int = -1
    var defaultAddress: Boolean = false
    var shippingName = ""

    @SerializedName(value = "recipientName", alternate = ["recepientName"])
    var recipientName = ""

    @SerializedName(value = "recipientMobile", alternate = ["recepientMobile"])
    var recipientMobile = ""

    var address = ""
    var zip = ""
    var roadAddress = ""
    var detailAddress = ""
    var shippingMessageType = ""
    var shippingMessage = ""
    var safetyMobile = true

    @Expose
    var addList = false // 배송지 목록에 추가 여부
}
