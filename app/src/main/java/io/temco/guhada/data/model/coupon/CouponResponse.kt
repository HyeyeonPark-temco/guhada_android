package io.temco.guhada.data.model.coupon

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.base.BasePageModel

class CouponResponse : BasePageModel() {
    @SerializedName("content")
    var content : MutableList<Coupon> = mutableListOf()

}