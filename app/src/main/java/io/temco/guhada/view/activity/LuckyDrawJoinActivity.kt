package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.databinding.ObservableBoolean
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CountTimer
import io.temco.guhada.data.viewmodel.LuckyDrawJoinViewModel
import io.temco.guhada.data.viewmodel.account.TermsViewModel
import io.temco.guhada.databinding.ActivityLuckydrawJoinBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.net.URLDecoder

/**
 * 럭키드로우 회원가입 Activity
 * @author Hyeyeon Park
 * @since 2019.11.12
 */
class LuckyDrawJoinActivity : BindActivity<ActivityLuckydrawJoinBinding>() {
    private lateinit var mViewModel: LuckyDrawJoinViewModel
    private lateinit var mTermsViewModel: TermsViewModel

    override fun getBaseTag(): String = LuckyDrawJoinActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_luckydraw_join

    override fun getViewType(): Type.View = Type.View.LUCKY_DRAW_JOIN

    override fun init() {
        mViewModel = LuckyDrawJoinViewModel().apply {
            this.mVerifyMobileTask = { startActivityForResult(Intent(this@LuckyDrawJoinActivity, VerifyPhoneActivity::class.java), RequestCode.VERIFY_PHONE.flag) }
            this.mNotifyJoinAvailableTask = {
                val isAllVerified = mViewModel.mIsEmailVerified.get() && mViewModel.mIsPasswordVerified.get() && mViewModel.mIsPasswordConfirmVerified.get() && mViewModel.mIsMobileVerified.get()
                mTermsViewModel.mIsJoinTermsChecked.set(mViewModel.mIsTermsAllChecked && isAllVerified)
                mTermsViewModel.notifyPropertyChanged(BR.mIsJoinTermsChecked)
            }
            this.mResetVerifyNumberTask = {
                mViewModel.mEmailVerifyNumber = ""
                mBinding.edittextLuckydrawjoinEmailverifynumber.text = ""
            }
        }
        mTermsViewModel = TermsViewModel().apply {
            this.mRedirectTermsTask = { type ->
                when (type) {
                    TermsViewModel.TermsType.PURCHASE.type -> CommonUtilKotlin.startTermsPurchase(this@LuckyDrawJoinActivity)
                    TermsViewModel.TermsType.PERSONAL.type -> CommonUtilKotlin.startTermsPersonal(this@LuckyDrawJoinActivity)
                    TermsViewModel.TermsType.SALES.type -> CommonUtilKotlin.startTermsSale(this@LuckyDrawJoinActivity)
                }
            }

            this.mCheckTermsTask = {
                val isAllChecked = this.user.agreeSaleTos && this.user.agreeEmailReception && this.user.agreeSmsReception && this.user.agreePurchaseTos && this.user.agreeCollectPersonalInfoTos
                val isAllVerified = mViewModel.mIsEmailVerified.get() && mViewModel.mIsPasswordVerified.get() && mViewModel.mIsPasswordConfirmVerified.get() && mViewModel.mIsMobileVerified.get()

                mViewModel.mIsJoinAvailable.set(isAllChecked && isAllVerified)
                mViewModel.mIsTermsAllChecked = isAllChecked
                this.mIsJoinTermsChecked.set(isAllChecked && isAllVerified)
                this.notifyPropertyChanged(BR.mIsJoinTermsChecked)
            }
        }

        mBinding.viewModel = mViewModel
        mBinding.includeLuckydrawjoinTerms.viewModel = mTermsViewModel
        mBinding.includeLuckydrawjoinTerms.setOnClickSignUp {
            mViewModel.signUpEventUser(agreeCollectPersonalInfoTos = mTermsViewModel.user.agreeCollectPersonalInfoTos, agreePurchaseTos = mTermsViewModel.user.agreePurchaseTos,
                    agreeSaleTos = mTermsViewModel.user.agreeSaleTos, agreeEmailReception = mTermsViewModel.user.agreeEmailReception, agreeSmsReception = mTermsViewModel.user.agreeSmsReception)
        }

        mBinding.includeLuckydrawjoinHeader.title = getString(R.string.join_title)
        mBinding.includeLuckydrawjoinHeader.setOnClickCloseButton { finish() }
        mBinding.executePendingBindings()
    }

    override fun onDestroy() {
        super.onDestroy()
        CountTimer.stopTimer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val name = data?.getStringExtra("name")
                    ?: getString(R.string.mypage_userinfo_passwaord_infotitle2)
            val phoneNumber = data?.getStringExtra("phoneNumber")
                    ?: getString(R.string.luckydraw_hint_mobile)
            val di = data?.getStringExtra("di") ?: ""
            val birth = data?.getStringExtra("birth") ?: ""
            val gender = data?.getStringExtra("gender") ?: ""

            mViewModel.mDiCode = di
            mViewModel.mBirth = birth
            mViewModel.mGender = gender
            mViewModel.mName.set(URLDecoder.decode(name))
            mViewModel.mMobile.set(phoneNumber)
            mViewModel.mIsMobileVerified = ObservableBoolean(true)

            mViewModel.notifyPropertyChanged(BR.mName)
            mViewModel.notifyPropertyChanged(BR.mMobile)
            mViewModel.notifyPropertyChanged(BR.mIsMobileVerified)
        }
    }
}