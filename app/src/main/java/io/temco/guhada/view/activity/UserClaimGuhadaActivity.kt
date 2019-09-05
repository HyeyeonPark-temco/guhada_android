package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.viewmodel.UserClaimGuhadaViewModel
import io.temco.guhada.databinding.ActivityUserclaimguhadaBinding
import io.temco.guhada.view.activity.base.BindActivity

class UserClaimGuhadaActivity : BindActivity<ActivityUserclaimguhadaBinding>() {

    private lateinit var mViewModel: UserClaimGuhadaViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    override fun getBaseTag(): String = this@UserClaimGuhadaActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_userclaimguhada
    override fun getViewType(): Type.View = Type.View.USERCLAIM_GUHADA

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = UserClaimGuhadaViewModel(this)
        mBinding.viewModel = mViewModel

        setViewInit()
    }


    private fun setViewInit() {

    }

}