package io.temco.guhada.data.model

import java.io.Serializable

class ItemOptionResponse : Serializable {
    var attribute1 = "" // 첫번째 옵션의 속성값
    var attribute2 = "" // 두번째 옵션의 속성값
    var attribute3 = "" // 세번째 옵션의 속성값

    var label1 = "" // 첫번째 옵션의 이름
    var label2 = "" // 두번째 옵션의 이름
    var label3 = "" // 세번째 옵션의 이름

    var dealOptionSelectId = 0 // 옵션의 아이디
    var price = 0 // 옵션의 가격(상품의 기본가격에서 해당금액만큼 더해준다. 음수값이 들어올 수도 있다.)
    var stock = 0 // 해당 상품의 재고
    var viewType = "" // 옵션 뷰 타입 ("SEPARATED", "INTEGRATED")


}
