package io.temco.guhada.data.model.cart

import io.temco.guhada.data.model.option.OptionAttr

class CartOption {
    var label: String? = ""
    var attrList = mutableListOf<OptionAttr>()
}