package io.temco.guhada.view.activity

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.viewmodel.VerifyEmailViewModel
import io.temco.guhada.databinding.ActivityVerifyuserinfoBinding

/**
 * 마이페이지-회원정보 수정(이메일 인증, 휴대폰 인증)
 * @author Hyeyeon Park
 */
class VerifyUserInfoActivity : AppCompatActivity() {
    private lateinit var mViewModel: VerifyEmailViewModel
    private lateinit var mBinding: ActivityVerifyuserinfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verifyuserinfo)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mViewModel = VerifyEmailViewModel().apply { this.mIsEmail = intent.getBooleanExtra("isEmail", false) }
        mViewModel.mOnSuccessVerify = {
            if (mViewModel.mIsEmail){
                ToastUtil.showMessage(getString(R.string.verifyuserinfo_emailmessage_success))
                intent.putExtra("email", mViewModel.mEmail.get())
            }else{
                ToastUtil.showMessage(getString(R.string.verifyuserinfo_mobilemessage_success))
                intent.putExtra("mobile", mViewModel.mMobile.get())
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        mBinding.includeVerifyemailHeader.title = if(mViewModel.mIsEmail) getString(R.string.verifyeuserinfo_emailtitle) else getString(R.string.verifyeuserinfo_mobiletitle)
        mBinding.includeVerifyemailHeader.setOnClickCloseButton { finish() }
        mBinding.viewModel = mViewModel
    }

}