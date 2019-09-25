package io.temco.guhada.common.listener

import io.temco.guhada.data.model.Category

interface OnCategorySearchListListener {
    fun onEvent(index : Int, list: List<Category>)
}