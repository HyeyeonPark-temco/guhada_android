package io.temco.guhada.common.listener

import io.temco.guhada.data.model.main.HomeDeal


interface OnMainCustomLayoutListener {
    fun updateDataList(tabIndex : Int, type : String)
    fun loadNewInDataList(tabIndex : Int, value : HomeDeal)
}