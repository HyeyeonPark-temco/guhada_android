package io.temco.guhada.data.model.option

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import java.io.Serializable

/**
 * 상품 옵션 정보 클래스
 * 웹에서 사용되는 형태로 생성되어 앱에서는 가공하여 사용해야 함
 * @author Hyeyeon Park
 */
class OptionInfo : Serializable {
    var attribute1: String? = ""            // 첫번째 옵션의 속성값
    var attribute2: String? = ""            // 두번째 옵션의 속성값
    var attribute3: String? = ""            // 세번째 옵션의 속성값

    var label1: String? = ""                // 첫번째 옵션의 이름
    var label2: String? = ""                // 두번째 옵션의 이름
    var label3: String? = ""                // 세번째 옵션의 이름

    var rgb1: String? = ""
    var rgb2: String? = ""
    var rgb3: String? = ""

    var dealOptionSelectId: Double = 0.0    // 옵션의 아이디
    var price = 0                           // 옵션의 가격(상품의 기본가격에서 해당금액만큼 더해준다. 음수값이 들어올 수도 있다.)
    var stock = 0                           // 해당 상품의 재고
    var viewType = ""                       // 옵션 뷰 타입 ("SEPARATED", "INTEGRATED")

    fun getOptionText(): String {
        var optionText = ""
        if (!attribute1.isNullOrEmpty())
            optionText = "$attribute1"

        if (!attribute2.isNullOrEmpty())
            optionText += ", $attribute2"

        if (!attribute3.isNullOrEmpty())
            optionText += ", $attribute3"

        return optionText
    }

    fun getOptionNameText(): String {
        var placeHolder = ""
        if (!label1.isNullOrEmpty())
            placeHolder = "$label1"

        if (!label2.isNullOrEmpty())
            placeHolder += ", $label2"

        if (!label3.isNullOrEmpty())
            placeHolder += ", $label3"

        return placeHolder
    }

    fun getExtraPriceText(): String {
        return if (price > 0) String.format(BaseApplication.getInstance().resources.getString(R.string.productdetail_option_extraprice_format), price) else ""
    }
}