package io.temco.guhada.data.model.order

import io.temco.guhada.data.model.option.OptionInfo
import java.io.Serializable

/**
 * @see Order
 * @see PurchaseOrder
 * @author Hyeyeon Park
 */
open class OrderItemResponse : Serializable {
    var cartItemId = 0L

    // SELLER
    var sellerId = 0L
    var sellerName = ""

    // CATEGORY
    var lcategoryId = 0
    var mcategoryId = 0
    var scategoryId = 0
    var dcategoryId = 0

    var lCategoryId = 0L
    var mCategoryId = 0L
    var sCategoryId = 0L
    var dCategoryId = 0L

    // BRAND
    var brandId = 0L
    var brandName = ""

    // PRODUCT
    var productId: Long  = 0
    var season = ""
    var dealId: Long = 0
    var dealName = ""
    var imageName = ""              // 대표 이미지 파일 명
    var imageUrl = ""               // 대표 이미지 URL
    var totalStock = 0
    var sellPrice = 0
    var discountPrice = 0           // 할인가 (판매가와 같은 경우는 할인이 안 됨을 의미함); 실제 상품의 판매 가격
    var couponDiscount = 0          // 쿠폰으로 할인된 가격
    var totalPaymentPrice = 0       // 총 결제금액
    var discountDiffPrice = 0       // 할인 적용 금액 (판매가 - 할인가)
    var pointPayment = 0            // 포인트
    var shipExpense = 0             // 배송비
    var shipExpenseTypeText = ""    // 배송비 상태 text (ex: 무료)
    var orderValidStatus = ""       // 상품 상태 ("VALID", "NOT_SALE", "NOT_DISPLAY", "SOLD_OUT", "CHANGE_PRICE", "CHANGE_OPTION")
    var quantity = 0                // 구매 수량

    var itemOptionResponse: OptionInfo? = OptionInfo()

    // 사용 가능 포인트 조회 API param
    var orderProdList = mutableListOf<OrderOption>()
    var productPrice = 0

    class OrderOption {
        var price = 0
    }

}

