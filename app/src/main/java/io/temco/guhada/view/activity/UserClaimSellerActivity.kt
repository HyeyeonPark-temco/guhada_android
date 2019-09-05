package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.viewmodel.UserClaimSellerViewModel
import io.temco.guhada.databinding.ActivityUserclaimsellerBinding
import io.temco.guhada.view.activity.base.BindActivity

class UserClaimSellerActivity : BindActivity<ActivityUserclaimsellerBinding>() {

    private lateinit var mViewModel: UserClaimSellerViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    override fun getBaseTag(): String = this@UserClaimSellerActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_userclaimseller
    override fun getViewType(): Type.View = Type.View.USERCLAIM_SELLER

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = UserClaimSellerViewModel(this)
        mBinding.viewModel = mViewModel

        setViewInit()
    }


    private fun setViewInit() {

    }

}