package io.temco.guhada.common.listener

import io.temco.guhada.data.model.Brand

interface OnProductDetailListener {
    fun scrollToElement(pos: Int)

    fun showMessage(message: String)
    fun showMenu()
    fun showAddCartResult()
    fun showSideMenu()

    fun hideLoadingIndicator()
    fun dismissOptionMenu()
    fun dismissAddCartResult()

    fun redirectPaymentActivity(isOptionPopupSelected: Boolean)
    fun redirectLoginActivity()
    fun redirectHome()
    fun closeActivity()

    fun setBrandProductList(brand: Brand)
    fun getSelectedOptionDealId() : Long?
    fun getSelectedProductQuantity() : Int
}