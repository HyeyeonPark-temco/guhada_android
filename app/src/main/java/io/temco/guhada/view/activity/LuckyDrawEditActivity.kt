package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CountTimer
import io.temco.guhada.data.model.Verification
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.data.model.user.SnsUser
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.LuckyDrawEditViewModel
import io.temco.guhada.data.viewmodel.LuckyDrawJoinViewModel
import io.temco.guhada.data.viewmodel.account.TermsViewModel
import io.temco.guhada.databinding.ActivityLuckydrawEditBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.net.URLDecoder

/**
 * 럭키드로우 회원정보 수정 Activity
 * @author Hyeyeon Park
 * @since 2019.11.12
 */
class LuckyDrawEditActivity : BindActivity<ActivityLuckydrawEditBinding>() {
    private lateinit var mViewModel: LuckyDrawEditViewModel
    private lateinit var mTermsViewModel: TermsViewModel

    override fun getBaseTag(): String = LuckyDrawEditActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_luckydraw_edit

    override fun getViewType(): Type.View = Type.View.LUCKY_DRAW_JOIN

    override fun init() {
        mViewModel = LuckyDrawEditViewModel().apply {
            // sns 유저 정보 수정
            intent.getSerializableExtra("snsUser").let {
                if (it != null) {
                    this.mSnsSignUp = it as EventUser.SnsSignUp
                    this.mEmail = it.email
                    this.mIsSns = true
                }
            }

            this.mVerifyMobileTask = { startActivityForResult(Intent(this@LuckyDrawEditActivity, VerifyPhoneActivity::class.java), RequestCode.VERIFY_PHONE.flag) }
            this.mNotifyJoinAvailableTask = {
                val isAllVerified = this.mIsEmailVerified.get() && this.mIsPasswordVerified.get() && this.mIsPasswordConfirmVerified.get() && this.mIsMobileVerified.get()
                this.mIsJoinAvailable.set(this.mIsTermsAllChecked && isAllVerified)
                mTermsViewModel.mIsJoinTermsChecked.set(this.mIsTermsAllChecked && isAllVerified)
                mTermsViewModel.notifyPropertyChanged(BR.mIsJoinTermsChecked)
            }
            this.mResetVerifyNumberTask = {
                this.mEmailVerifyNumber = ""
                mBinding.edittextLuckydrawjoinEmailverifynumber.text = ""
            }

            this.mEventUser.observe(this@LuckyDrawEditActivity, Observer {
                mBinding.eventUser = it
                mBinding.executePendingBindings()

                if (!it.email.isNullOrEmpty() && it.validEmail) {
                    this.mEmail = it.email
                    mBinding.edittextLuckydrawjoinEmail.text = it.email
                }

                if (!it.identityVerify.mobile.isNullOrEmpty()) {
                    this.mMobile = ObservableField(it.identityVerify.mobile)
                    mBinding.textviewLuckydrawjoinMobile.text = it.identityVerify.mobile
                }

                if (!it.identityVerify.name.isNullOrEmpty()) {
                    this.mName = ObservableField(it.identityVerify.name)
                    mBinding.textviewLuckydrawjoinName.text = it.identityVerify.name
                }

                if (it.emailVerified) {
                    mBinding.buttonLuckydrawjoinSendemailverify.text = getString(R.string.luckydraw_finishverify)
                    mViewModel.mIsEmailVerified = ObservableBoolean(true)
                    mViewModel.mIsEmailDuplicate = ObservableBoolean(true)
                }

                mViewModel.mIsMobileVerified = ObservableBoolean(it.identityVerify.identityVerifyMethod != Verification.IdentityVerifyMethod.NONE.code)
                mViewModel.mIsPasswordVerified = ObservableBoolean(true)
                mViewModel.mIsPasswordConfirmVerified = ObservableBoolean(true)

                mBinding.includeLuckydrawjoinTerms.checkboxJoinPrivacy.isChecked = it.acceptTerms.agreeCollectPersonalInfoTos
                mBinding.includeLuckydrawjoinTerms.checkboxJoinBuy.isChecked = it.acceptTerms.agreePurchaseTos
                mBinding.includeLuckydrawjoinTerms.checkboxJoinSale.isChecked = it.acceptTerms.agreeSaleTos
                mBinding.includeLuckydrawjoinTerms.checkboxJoinEmail.isChecked = it.acceptTerms.agreeEmailReception
                mBinding.includeLuckydrawjoinTerms.checkboxJoinSms.isChecked = it.acceptTerms.agreeSmsReception
            })
        }

        mTermsViewModel = TermsViewModel().apply {
            this.mRedirectTermsTask = { type ->
                when (type) {
                    TermsViewModel.TermsType.PURCHASE.type -> CommonUtilKotlin.startTermsPurchase(this@LuckyDrawEditActivity)
                    TermsViewModel.TermsType.PERSONAL.type -> CommonUtilKotlin.startTermsPersonal(this@LuckyDrawEditActivity)
                    TermsViewModel.TermsType.SALES.type -> CommonUtilKotlin.startTermsSale(this@LuckyDrawEditActivity)
                }
            }

            this.mCheckTermsTask = {
                val isAllChecked = this.user.agreeSaleTos && this.user.agreeEmailReception && this.user.agreeSmsReception && this.user.agreePurchaseTos && this.user.agreeCollectPersonalInfoTos
                val isAllVerified = mViewModel.mIsEmailVerified.get() && mViewModel.mIsPasswordVerified.get() && mViewModel.mIsPasswordConfirmVerified.get() && mViewModel.mIsMobileVerified.get()

                mViewModel.mIsJoinAvailable.set(isAllChecked && isAllVerified)
                mViewModel.mIsTermsAllChecked = isAllChecked

                if(isAllChecked){
                    mViewModel.mEventUser.value?.acceptTerms?.agreeCollectPersonalInfoTos = true
                    mViewModel.mEventUser.value?.acceptTerms?.agreePurchaseTos = true
                    mViewModel.mEventUser.value?.acceptTerms?.agreeSaleTos = true
                    mViewModel.mEventUser.value?.acceptTerms?.agreeEmailReception = true
                    mViewModel.mEventUser.value?.acceptTerms?.agreeSmsReception = true
                }

                // "회원가입" 버튼 활성화 field
                this.mIsJoinTermsChecked.set(isAllChecked && isAllVerified)
                this.notifyPropertyChanged(BR.mIsJoinTermsChecked)
            }
        }

        mBinding.viewModel = mViewModel
        mBinding.includeLuckydrawjoinTerms.viewModel = mTermsViewModel
        mBinding.includeLuckydrawjoinTerms.setOnClickSignUp {
            mViewModel.updateEventUser()
        }
        mBinding.includeLuckydrawjoinHeader.title = getString(R.string.luckydraw_title_edit)
        mBinding.includeLuckydrawjoinHeader.setOnClickCloseButton { finish() }
        mBinding.executePendingBindings()

        mViewModel.getEventUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        CountTimer.stopTimer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.VERIFY_PHONE.flag) {
            val name = data?.getStringExtra("name")
                    ?: getString(R.string.mypage_userinfo_passwaord_infotitle2)
            val mobile = data?.getStringExtra("phoneNumber")
                    ?: getString(R.string.luckydraw_hint_mobile)
            val di = data?.getStringExtra("di") ?: ""
            val birth = data?.getStringExtra("birth") ?: ""
            val gender = data?.getStringExtra("gender") ?: ""

            mViewModel.mDiCode = di
            mViewModel.mBirth = birth
            mViewModel.mGender = gender
            mViewModel.mName.set(URLDecoder.decode(name))
            mViewModel.mMobile.set(mobile)
            mViewModel.mIsMobileVerified = ObservableBoolean(true)

            mViewModel.notifyPropertyChanged(BR.mName)
            mViewModel.notifyPropertyChanged(BR.mMobile)
            mViewModel.notifyPropertyChanged(BR.mIsMobileVerified)

            mViewModel.mEventUser.value?.verifiedIdentityUpdated = true
            mViewModel.mEventUser.value?.identityVerify?.name = name
            mViewModel.mEventUser.value?.identityVerify?.mobile = mobile
            mViewModel.mEventUser.value?.identityVerify?.birth = birth
            mViewModel.mEventUser.value?.identityVerify?.gender = gender
            mViewModel.mEventUser.value?.identityVerify?.diCode = di
            mViewModel.mEventUser.value?.identityVerify?.identityVerifyMethod = Verification.IdentityVerifyMethod.MOBILE.code
        }
    }
}