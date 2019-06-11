package io.temco.guhada.common.listener

interface OnProductDetailListener {
    fun scrollToElement(pos: Int)
    fun showMessage(message: String)
    fun showMenu()
    fun redirectPaymentActivity(isOptionPopupSelected: Boolean)
}