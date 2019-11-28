package io.temco.guhada.view.activity

import android.app.Activity
import android.text.TextUtils
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.blockchain.TokenAddress
import io.temco.guhada.data.viewmodel.GuhadaTokenAddressCreateDialogViewModel
import io.temco.guhada.databinding.ActivityGuhadatokenaddresscreatedialogBinding
import io.temco.guhada.view.activity.base.BindActivity

class GuhadaTokenAddressCreateDialog : BindActivity<ActivityGuhadatokenaddresscreatedialogBinding>() {
    private lateinit var mViewModel: GuhadaTokenAddressCreateDialogViewModel

    override fun getBaseTag(): String = GuhadaTokenAddressCreateDialog::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_guhadatokenaddresscreatedialog
    override fun getViewType(): Type.View = Type.View.GUHADA_TOKEN

    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    private var tokenName : String = ""

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)

        mViewModel = GuhadaTokenAddressCreateDialogViewModel(this)
        mBinding.viewModel = mViewModel
        mBinding.setOnClickCloseButton { onBackPressed() }

        tokenName = intent?.extras?.getString("tokenName") ?:""
        if(TextUtils.isEmpty(tokenName)) finish()

        mBinding.relativeGuhadatokenContent.visibility = View.GONE
        getData()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.dismiss()
    }

    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////

    private fun getData(){
        mLoadingIndicatorUtil.show()
        mViewModel.getTokenAddress(tokenName, object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mLoadingIndicatorUtil.dismiss()
                mBinding.relativeGuhadatokenContent.visibility = View.VISIBLE
                var address = value as TokenAddress
                if(CustomLog.flag)CustomLog.L("GuhadaTokenAddressCreateDialog","getTokenAddress",address.toString())

                mBinding.executePendingBindings()
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////

}