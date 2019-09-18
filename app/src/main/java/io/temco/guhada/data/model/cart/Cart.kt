package io.temco.guhada.data.model.cart

import com.google.gson.annotations.Expose
import io.temco.guhada.data.model.option.OptionInfo

/**
 * 장바구니 상품 클래스
 * @author Hyeyeon Park
 */
class Cart {
    // ID
    var dealId: Long = 0
    var sellerId: Long = 0
    var cartItemId: Long = 0

    // IMAGE
    var imageName: String = ""
    var imageUrl: String = ""

    // PRICE
    var sellPrice: Int = 0
    var discountDiffPrice: Int = 0
    var discountPrice: Int = 0
    var discountRate : Double = 0.0
    var shipExpense: Int = 0

    // QUANTITY
    var currentQuantity: Int = 0
    var maxQuantity: Int = 0
    var minQuantity: Int = 0

    // PRODUCT
    var brandName: String = ""
    var dealName: String = ""
    var season: String = ""

    var cartValidStatus: CartValidStatus = CartValidStatus()
    var selectedCartOption: OptionInfo? = OptionInfo()
    var dealUrl: String = ""
    var sellerName: String = ""
    var priority: Int = 0
    var totalStock: Int = 0

    @Expose
    var cartOptionList: MutableList<CartOption> = mutableListOf()

    @Expose
    var cartOptionInfoList: MutableList<OptionInfo> = mutableListOf()

    // 상품 수량 변경시 사용되는 더미 필드
    @Expose
    var tempQuantity = 0

    fun getOptionStr(): String {
        var result = ""
        if (selectedCartOption?.attribute1 != null) {
            result = "$result${selectedCartOption?.attribute1}"
        }

        if (selectedCartOption?.attribute2 != null) {
            result = "$result, ${selectedCartOption?.attribute2}"
        }

        if (selectedCartOption?.attribute3 != null) {
            result = "$result, ${selectedCartOption?.attribute3}"
        }

        result = if (result.isEmpty()) "${currentQuantity}개"
        else "$result, ${currentQuantity}개"

        return result
    }

}
