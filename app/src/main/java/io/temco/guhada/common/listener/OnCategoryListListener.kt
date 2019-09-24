package io.temco.guhada.common.listener

import io.temco.guhada.data.model.Category

interface OnCategoryListListener {
    fun onEvent(index : Int, category: Category)
}