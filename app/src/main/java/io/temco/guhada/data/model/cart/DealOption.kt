package io.temco.guhada.data.model.cart

/**
 * 상품의 옵션 id 정보 클래스
 * @see CartDealOption
 * @author Hyeyeon Park
 */
 class DealOption {
    var dealOptionId = 0
    var optionMap: MutableMap<String, String> = mutableMapOf()
}