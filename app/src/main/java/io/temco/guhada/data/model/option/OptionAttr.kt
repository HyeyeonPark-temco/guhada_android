package io.temco.guhada.data.model.option

import java.io.Serializable

class OptionAttr : Serializable {
    var dealOptionId = 0 // 장바구니 API 호출 파라미터
    var rgb: String = ""
    var name: String = "" // label
}