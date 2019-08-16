package io.temco.guhada.data.model.user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserSize : Serializable {
    @SerializedName("height")
    var height : Int = 0

    @SerializedName("weight")
    var weight : Int = 0

    @SerializedName("top")
    var top : String = ""

    @SerializedName("bottom")
    var bottom : Int = 0

    @SerializedName("shoe")
    var shoe : Int = 0

    override fun toString(): String {
        return "UserSize(height=$height, weight=$weight, top='$top', bottom=$bottom, shoe=$shoe)"
    }
}