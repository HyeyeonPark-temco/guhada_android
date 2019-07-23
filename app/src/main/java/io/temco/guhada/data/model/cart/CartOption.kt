package io.temco.guhada.data.model.cart

import io.temco.guhada.data.model.option.OptionAttr

/**
 * 상품 보유 옵션 클래스
 * @author Hyeyeon Park
 */
class CartOption {
    var label: String? = ""
    var attrList = mutableListOf<OptionAttr>()
}