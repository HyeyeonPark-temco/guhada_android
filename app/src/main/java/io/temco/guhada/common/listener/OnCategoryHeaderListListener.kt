package io.temco.guhada.common.listener

import io.temco.guhada.data.model.Category

interface OnCategoryHeaderListListener {
    fun onEvent(index : Int, category: Category)
}