package io.temco.guhada.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 주문 취소 신청 사유 클래스
 * @author Hyeyeon Park
 * @since 2019.08.07
 */
class OrderChangeCause : Serializable{
    var code : String = ""

    @SerializedName("contents")
    var label : String = ""

    @SerializedName("userFault")
    var isFeeCharged : Boolean = true
}