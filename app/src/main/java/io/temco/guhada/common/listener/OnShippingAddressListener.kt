package io.temco.guhada.common.listener

import io.temco.guhada.data.model.UserShipping

interface OnShippingAddressListener {
    fun closeActivity(resultCode : Int, shippingAddress: UserShipping?)
    fun notifyDeleteItem()
    fun redirectEditShippingAddressActivity(shippingAddress: UserShipping)

}