package io.temco.guhada.common.listener

import io.temco.guhada.data.model.UserShipping

interface OnShippingAddressListener {
    fun closeActivity(resultCode : Int, withExtra : Boolean)
    fun notifyDeleteItem()
    fun redirectEditShippingAddressActivity(shippingAddress: UserShipping)

}