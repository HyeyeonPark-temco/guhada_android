package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.databinding.ActivityCustomviewBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.custom.CustomSpinnerView

class CustomViewActivity : BindActivity<ActivityCustomviewBinding>() {
    override fun getBaseTag(): String = CustomViewActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_customview

    override fun getViewType(): Type.View = Type.View.TEST

    override fun init() {
        mBinding.list = mutableListOf("111111", "2222", "333")
        mBinding.spinnerView.setItems(mutableListOf("1"))
        mBinding.executePendingBindings()
    }
}