package io.temco.guhada.common.listener

import io.temco.guhada.view.adapter.ProductDetailOptionAdapter

interface OnProductDetailListener {
    fun scrollToElement(pos: Int)
    fun notifyOptionAttrAdapter()
    fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr)
    fun showMessage(message: String)
}