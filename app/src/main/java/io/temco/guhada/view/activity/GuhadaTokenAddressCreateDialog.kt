package io.temco.guhada.view.activity

import android.app.Activity
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.viewmodel.GuhadaTokenAddressCreateDialogViewModel
import io.temco.guhada.databinding.ActivityGuhadatokenaddresscreatedialogBinding
import io.temco.guhada.view.activity.base.BindActivity

class GuhadaTokenAddressCreateDialog : BindActivity<ActivityGuhadatokenaddresscreatedialogBinding>() {
    private lateinit var mViewModel: GuhadaTokenAddressCreateDialogViewModel

    override fun getBaseTag(): String = CustomDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_guhadatokenaddresscreatedialog
    override fun getViewType(): Type.View = Type.View.REVIEW_POINT_DIALOG

    override fun init() {
        mViewModel = GuhadaTokenAddressCreateDialogViewModel(this)
        mBinding.viewModel = mViewModel


        mBinding.executePendingBindings()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    ////////////////////////////////////////////////////////////////////////////////
}