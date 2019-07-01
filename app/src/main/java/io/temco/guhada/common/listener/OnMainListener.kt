package io.temco.guhada.common.listener

import io.temco.guhada.data.model.Brand

interface OnMainListener {
    fun removeProductFragment()
    fun removeProductDetailFragment()
    fun showSideMenu(isOpen : Boolean )
    fun setBrandProductList(brand : Brand)
}