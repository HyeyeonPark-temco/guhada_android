package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.databinding.ActivityPaymentResultBinding
import io.temco.guhada.view.activity.base.BindActivity

class PaymentResultActivity : BindActivity<ActivityPaymentResultBinding>() {
    override fun getBaseTag(): String = PaymentResultActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_payment_result

    override fun getViewType(): Type.View = Type.View.PAYMENT_RESULT

    override fun init() {

    }
}