package io.temco.guhada.common.listener

import io.temco.guhada.data.model.OptionAttr

interface OnProductDetailMenuListener {
    fun showMessage(message: String)
    fun closeMenu()
    fun setColorName(optionAttr: OptionAttr, task: () -> Unit)
}