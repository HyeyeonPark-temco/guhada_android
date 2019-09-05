package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableBoolean
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.util.ToastUtil
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
            this.mEmail = intent.getStringExtra("email") ?: ""
            this.mName = intent.getStringExtra("name") ?: ""
            this.mVerifyPhoneTask = {
                val intent = Intent(this@VerifyActivity, VerifyPhoneActivity::class.java)
                this@VerifyActivity.startActivityForResult(intent, RequestCode.VERIFY_PHONE.flag)
            }
        }
    }

    private fun initHeader() {
        mBinding.includeVerifyHeader.title = resources.getString(R.string.verify_title)
        mBinding.includeVerifyHeader.onClickBackButton = View.OnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.VERIFY_PHONE.flag) {
            if (resultCode == Activity.RESULT_OK) {
                val name = data?.getStringExtra("name")
                val phoneNumber = data?.getStringExtra("phoneNumber")
                val di = data?.getStringExtra("di")

                mViewModel.mMobileVerification = ObservableBoolean(true)
                mViewModel.notifyPropertyChanged(BR.mMobileVerification)
            }
        }
    }
}