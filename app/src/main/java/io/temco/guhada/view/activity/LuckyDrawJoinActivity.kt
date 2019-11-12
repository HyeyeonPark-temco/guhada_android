package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.data.viewmodel.LuckyDrawJoinViewModel
import io.temco.guhada.data.viewmodel.account.TermsViewModel
import io.temco.guhada.databinding.ActivityLuckydrawJoinBinding
import io.temco.guhada.view.activity.base.BindActivity

class LuckyDrawJoinActivity : BindActivity<ActivityLuckydrawJoinBinding>() {
    private lateinit var mViewModel: LuckyDrawJoinViewModel

    override fun getBaseTag(): String = LuckyDrawJoinActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_luckydraw_join

    override fun getViewType(): Type.View = Type.View.LUCKY_DRAW_JOIN

    override fun init() {
        mViewModel = LuckyDrawJoinViewModel()
        mBinding.viewModel = mViewModel
        mBinding.includeLuckydrawjoinTerms.viewModel = TermsViewModel().apply {
            this.mRedirectTermsTask = { type ->
                when (type) {
                    TermsViewModel.TermsType.PURCHASE.type -> CommonUtilKotlin.startTermsPurchase(this@LuckyDrawJoinActivity)
                    TermsViewModel.TermsType.PERSONAL.type -> CommonUtilKotlin.startTermsPersonal(this@LuckyDrawJoinActivity)
                    TermsViewModel.TermsType.SALES.type -> CommonUtilKotlin.startTermsSale(this@LuckyDrawJoinActivity)
                }
            }
        }

        mBinding.includeLuckydrawjoinHeader.title = getString(R.string.join_title)
        mBinding.includeLuckydrawjoinHeader.setOnClickCloseButton { finish() }
        mBinding.executePendingBindings()
    }
}