package io.temco.guhada.data.model.option

import com.google.gson.annotations.Expose
import java.io.Serializable

class OptionAttr : Serializable {
    var dealOptionId = 0 // 장바구니 API 호출 파라미터
    var rgb: String = ""
    var name: String = ""

    @Expose
    var label: String = ""
}