package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.Inquiry
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.viewmodel.WriteClaimViewModel
import io.temco.guhada.databinding.ActivityWriteclaimBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 상품 문의 작성
 * @author Hyeyeon Park
 */
class WriteClaimActivity : BindActivity<ActivityWriteclaimBinding>() {
    private var mViewModel: WriteClaimViewModel = WriteClaimViewModel()

    override fun getBaseTag(): String = WriteClaimActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_writeclaim
    override fun getViewType(): Type.View = Type.View.CLAIM

    override fun init() {
        mViewModel.closeActivity = { resultCode, claim -> closeActivity(resultCode, claim) }

        // 문의 수정
        val inquiry = intent.getSerializableExtra("inquiry")
        if (inquiry != null) {
            val prevInquiry = inquiry as Inquiry
            mViewModel.inquiry = prevInquiry
        }

        // 문의 등록
        val productId = intent.getLongExtra("productId", -1)
        if (productId > -1) {
            mViewModel.inquiry.productId = productId
            mBinding.includeWriteclaimHeader.viewModel = mViewModel
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        } else {
            closeActivity(RESULT_CANCELED, null)
        }
    }

    fun closeActivity(resultCode: Int, claim: Claim?) {
        if (claim != null) intent.putExtra("inquiry", claim)
        setResult(resultCode, intent)
        this@WriteClaimActivity.finish()
    }

}