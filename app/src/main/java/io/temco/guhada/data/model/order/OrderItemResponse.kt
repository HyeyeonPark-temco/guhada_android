package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.option.OptionInfo
import java.io.Serializable

open class OrderItemResponse : Serializable {
    var cartItemId = 0

    // SELLER
    var sellerId = 0
    var sellerName = ""

    // CATEGORY
    var lcategoryId = 0
    var mcategoryId = 0
    var scategoryId = 0
    var dcategoryId = 0

    var lCategoryId = 0
    var mCategoryId = 0
    var sCategoryId = 0
    var dCategoryId = 0

    // BRAND
    var brandId = 0
    var brandName = ""

    // PRODUCT
    var productId: Long = 0
    var season = ""
    var dealId: Long = 0
    var dealName = ""
    var imageName = "" // 대표 이미지 파일 명
    var imageUrl = "" // 대표 이미지 URL
    var totalStock = 0
    var sellPrice = 0
    var discountPrice = 0 // 할인가 (판매가와 같은 경우는 할인이 안 됨을 의미함)
    var discountDiffPrice = 0 // 할인 적용 금액 (판매가 - 할인가)
    var shipExpense = 0
    var orderValidStatus = "" // 상품 상태 ("VALID", "NOT_SALE", "NOT_DISPLAY", "SOLD_OUT", "CHANGE_PRICE", "CHANGE_OPTION")
    var quantity = 0 // 구매 수량
    var optionInfo = OptionInfo()
}

