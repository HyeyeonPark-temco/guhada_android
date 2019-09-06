package io.temco.guhada.data.model.product

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.option.OptionAttr
import java.io.Serializable

/**
 * 상품의 기본 정보 클래스
 * @see Product
 * @author Hyeyeon Park
 */
open class BaseProduct : Serializable {
    var season: String = ""

    var productId: Long = 0

    var dealId: Long = 0

    var brandName: String = ""

    @SerializedName("name")
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
    @Expose
    var totalPrice: Int = 0

    @Expose
    var totalCount: Int = 0

    @Expose
    var optionMap: MutableMap<String, OptionAttr> = mutableMapOf()

    @Expose
    var dealOptionId: Long? = null

}