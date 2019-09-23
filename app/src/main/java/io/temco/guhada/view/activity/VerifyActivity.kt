package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableBoolean
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.viewmodel.VerifyViewModel
import io.temco.guhada.databinding.ActivityVerifyBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 주문결제-본인인증 (휴대폰, 이메일)
 * @author Hyeyeon Park
 */
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
            this.mEmailVerification = ObservableBoolean(intent.getBooleanExtra("emailVerification", false))
            this.mMobileVerification = ObservableBoolean(intent.getBooleanExtra("mobileVerification", false))
            intent.getSerializableExtra("user").let {
                this.mUser =
                        if (it != null) it as User
                        else User()
            }
            this.mVerifyPhoneTask = {
                val intent = Intent(this@VerifyActivity, VerifyPhoneActivity::class.java)
                this@VerifyActivity.startActivityForResult(intent, RequestCode.VERIFY_PHONE.flag)
            }
        }
    }

    private fun initHeader() {
        mBinding.includeVerifyHeader.title = resources.getString(R.string.verify_title)
        mBinding.includeVerifyHeader.onClickBackButton = View.OnClickListener { closeActivity() }
    }

    override fun onBackPressed() = closeActivity()

    private fun closeActivity() {
        if(::mViewModel.isInitialized){
            this@VerifyActivity.intent.putExtra("mobileVerification", mViewModel.mMobileVerification.get())
            this@VerifyActivity.intent.putExtra("emailVerification", mViewModel.mEmailVerification.get())
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.VERIFY_PHONE.flag) {
            if (resultCode == Activity.RESULT_OK) {
                mViewModel.mVerification.name = data?.getStringExtra("name")?:""
                mViewModel.mVerification.mobile = data?.getStringExtra("phoneNumber")?:""
                mViewModel.mVerification.diCode = data?.getStringExtra("di")?:""
                mViewModel.mVerification.gender =  data?.getStringExtra("gender")?:""
                mViewModel.updateIdentityVerify()
            }
        }
    }
}