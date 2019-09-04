package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.viewmodel.VerifyViewModel
import io.temco.guhada.databinding.ActivityVerifyBinding
import io.temco.guhada.view.activity.base.BindActivity

class VerifyActivity : BindActivity<ActivityVerifyBinding>() {
    private lateinit var mViewModel: VerifyViewModel

    override fun getBaseTag(): String = VerifyActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_verify
    override fun getViewType(): Type.View = Type.View.VERIFY

    override fun init() {
        initHeader()
        initViewModel()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = VerifyViewModel().apply {
            this.mEmail = intent.getStringExtra("email")
            this.mName = intent.getStringExtra("name")
        }
    }

    private fun initHeader() {
        mBinding.includeVerifyHeader.title = resources.getString(R.string.verify_title)
        mBinding.includeVerifyHeader.setOnClickCloseButton { finish() }
    }
}