package io.temco.guhada.data.model.option

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

    override fun toString(): String {
        return "OptionInfo(attribute1=$attribute1, attribute2=$attribute2, attribute3=$attribute3, label1=$label1, label2=$label2, label3=$label3, rgb1=$rgb1, rgb2=$rgb2, rgb3=$rgb3, dealOptionSelectId=$dealOptionSelectId, price=$price, stock=$stock, viewType='$viewType')"
    }

}