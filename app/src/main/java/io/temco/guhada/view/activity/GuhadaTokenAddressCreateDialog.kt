package io.temco.guhada.view.activity

import android.app.Activity
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.viewmodel.GuhadaTokenAddressCreateDialogViewModel
import io.temco.guhada.databinding.ActivityGuhadatokenaddresscreatedialogBinding
import io.temco.guhada.view.activity.base.BindActivity

class GuhadaTokenAddressCreateDialog : BindActivity<ActivityGuhadatokenaddresscreatedialogBinding>() {
    private lateinit var mViewModel: GuhadaTokenAddressCreateDialogViewModel

    override fun getBaseTag(): String = GuhadaTokenAddressCreateDialog::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_guhadatokenaddresscreatedialog
    override fun getViewType(): Type.View = Type.View.GUHADA_TOKEN

    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = GuhadaTokenAddressCreateDialogViewModel(this)
        mBinding.viewModel = mViewModel
        mBinding.setOnClickCloseButton { onBackPressed() }

        mViewModel.getTokenList(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag)CustomLog.L("GuhadaTokenAddressCreateDialog","getTokenList",value.toString())
            }
        })
        mBinding.executePendingBindings()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    ////////////////////////////////////////////////////////////////////////////////
}