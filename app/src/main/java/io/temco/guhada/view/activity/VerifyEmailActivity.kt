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
import io.temco.guhada.databinding.ActivityVerifyemailBinding

class VerifyEmailActivity : AppCompatActivity() {
    private lateinit var mViewModel: VerifyEmailViewModel
    private lateinit var mBinding: ActivityVerifyemailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verifyemail)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mViewModel = VerifyEmailViewModel()
        mViewModel.mOnSuccessVerify = {
            ToastUtil.showMessage(getString(R.string.verifyemail_message_success))
            intent.putExtra("email", mViewModel.mEmail.get())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        mBinding.includeVerifyemailHeader.title = getString(R.string.verifyemail_title)
        mBinding.includeVerifyemailHeader.setOnClickCloseButton { finish() }
        mBinding.viewModel = mViewModel
    }

}