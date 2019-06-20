package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 배송지 변경 Activity
 */
class ShippingAddressActivity : BindActivity<io.temco.guhada.databinding.ActivityShippingaddressBinding>() {
    override fun getBaseTag(): String = ShippingAddressActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_shippingaddress

    override fun getViewType(): Type.View = Type.View.SHIPPING_ADDRESS

    override fun init() {

    }
}