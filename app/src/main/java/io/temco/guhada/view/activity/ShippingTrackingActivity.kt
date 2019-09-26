package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.view.activity.base.BindActivity

class ShippingTrackingActivity : BindActivity<io.temco.guhada.databinding.ActivityShippingtrackingBinding>() {
    override fun getBaseTag(): String = ShippingTrackingActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_shippingtracking
    override fun getViewType(): Type.View = Type.View.TEST
    override fun init() {

    }
}