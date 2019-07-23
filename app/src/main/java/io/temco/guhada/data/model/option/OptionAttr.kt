package io.temco.guhada.data.model.option

import com.google.gson.annotations.Expose
import java.io.Serializable

/**
 * 상품 옵션의 attribute 값 클래스
 * @author Hyeyeon Park
 */
class OptionAttr : Serializable {
    var dealOptionId = 0
    var rgb: String = ""
    var name: String = ""

    @Expose
    var label: String = ""
}