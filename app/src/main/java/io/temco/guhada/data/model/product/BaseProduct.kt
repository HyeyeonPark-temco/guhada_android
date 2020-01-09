package io.temco.guhada.data.model.product

import com.google.gson.annotations.Expose
import io.temco.guhada.data.model.option.OptionAttr
import java.io.Serializable

/**
 * 상품의 기본 정보 클래스
 * @see Product
 * @author Hyeyeon Park
 */
open class BaseProduct : Serializable {
    var season: String = ""

    var productId: Long = 0L

    var dealId: Long = 0L

    var brandName: String = ""

    var name: String = ""

    var profileUrl: String = ""

    var optionStr: String = ""
//      GRID LIST (DEPRECATED)
//        get() {
//            return if (field.isEmpty()) {
//                var result = ""
//                for (key in optionMap.keys) {
//                    result = if (result.isEmpty()) "${optionMap[key]?.name}"
//                    else "$result, ${optionMap[key]?.name}"
//                }
//
//                result = if (result.isEmpty()) "${totalCount}개"
//                else "$result, ${totalCount}개"
//                result
//            } else {
//                field
//            }
//        }

    var totalPrice: Int = 0
    var sellPrice = 0
    var discountPrice = 0

    @Expose
    var totalCount: Int = 0

    @Expose
    var optionMap: MutableMap<String, OptionAttr> = mutableMapOf()

    @Expose
    var dealOptionId: Long? = null


}