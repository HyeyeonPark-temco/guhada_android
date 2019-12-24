package io.temco.guhada.data.model.body

import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 *
 * 상품목록 검색 설정에서 설정한 기타 검색 조건
 */

class FilterEtcBody {

    var shippingConditionFlag1 = false // 해외배송
    var shippingConditionFlag2 = false // 국내배송

    var productConditionFlag1 = false // 새상품
    var productConditionFlag2 = false // 중고

    var priceConditionIndex = -1
    var priceConditionMin = 0
    var priceConditionMax = 0

    var searchWord = ""

    override fun toString(): String {
        if(CustomLog.flag)return "FilterEtcBody(shippingConditionFlag1=$shippingConditionFlag1, shippingConditionFlag2=$shippingConditionFlag2, productConditionFlag1=$productConditionFlag1, productConditionFlag2=$productConditionFlag2, priceConditionMin=$priceConditionMin, priceConditionMax=$priceConditionMax, searchWord='$searchWord')"
        else return ""
    }

}

enum class FilterEtcType(val code : String, var label:String) {
    INTERNATIONAL("INTERNATIONAL", "해외배송"),
    NATIONAL("NATIONAL", "국내배송"),
    NEW("NEW", "새상품"),
    USED("USED", "빈티지"),
    PRICE_ALL("PRICE_ALL", "모두보기"),
    PRICE_10("PRICE_10", "10만원 이하"),
    PRICE_30("PRICE_30", "30만원 이하"),
    PRICE_50("PRICE_50", "50만원 이하"),
    PRICE_100("PRICE_100", "100만원 이하"),
    PRICE_CUSTOM("PRICE_CUSTOM", ""),
    SEARCHWORD("SEARCHWORD", "결과 내 검색"),
}

data class FilterEtcModel(var type : FilterEtcType,
                          var value : Any,
                          var title : String)
