package io.temco.guhada.common.listener

import io.temco.guhada.view.adapter.ProductDetailOptionAdapter

interface OnProductDetailMenuListener {
    fun showMessage(message: String)
    fun closeMenu()
    fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr, task: () -> Unit)
}