package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.util.Log
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.data.viewmodel.account.TermsViewModel
import io.temco.guhada.databinding.ActivityTermsBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * SNS 회원가입 약관 동의 Activity
 * @author Hyeyeon Park
 */
class TermsActivity : BindActivity<ActivityTermsBinding>() {
    private lateinit var mViewModel: TermsViewModel

    override fun getBaseTag(): String = TermsActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_terms
    override fun getViewType(): Type.View = Type.View.TERMS

    override fun init() {
        mViewModel = TermsViewModel().apply {
            this.mCloseTask = { resultCode ->
                if (resultCode == Activity.RESULT_OK) {
                    val dialogIntent = Intent(this@TermsActivity, CustomDialogActivity::class.java)
                    dialogIntent.putExtra("email", intent.getStringExtra("email") ?: "")
                    this@TermsActivity.startActivityForResult(dialogIntent, Flag.RequestCode.WELCOME_DIALOG)
                } else {
                    finish()
                }
            }
        }
        mBinding.viewModel = mViewModel
        mBinding.includeTerms.viewModel = mViewModel
        mBinding.includeTermsHeader.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun setIntentExtras(resultCode: Int) {
        intent.putExtra("agreeCollectPersonalInfoTos", mViewModel.user.agreeCollectPersonalInfoTos)
        intent.putExtra("agreeEmailReception", mViewModel.user.agreeEmailReception)
        intent.putExtra("agreePurchaseTos", mViewModel.user.agreePurchaseTos)
        intent.putExtra("agreeSaleTos", mViewModel.user.agreeSaleTos)
        intent.putExtra("agreeSmsReception", mViewModel.user.agreeSmsReception)
        intent.putExtra("email", intent.getStringExtra("email") ?: "")
        setResult(resultCode, intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Flag.RequestCode.WELCOME_DIALOG && resultCode == Activity.RESULT_OK) {
            setIntentExtras(resultCode)
            finish()
        }
    }
}