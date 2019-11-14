package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.databinding.ActivityLuckyeventdetailBinding
import io.temco.guhada.view.activity.base.BindActivity

class LuckyEventDialogActivity : BindActivity<ActivityLuckyeventdetailBinding>() {

    override fun getBaseTag(): String = this@LuckyEventDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_luckyeventdialog
    override fun getViewType(): Type.View = Type.View.LUCKY_EVENT_DIALOG

    override fun init() {

    }

}