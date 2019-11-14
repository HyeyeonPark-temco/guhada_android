package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.internal.LinkedTreeMap
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.enum.VerificationType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnTimerListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Verification
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 럭키드로우 회원가입 ViewModel
 * @author Hyeyeon Park
 */
class LuckyDrawJoinViewModel : BaseObservableViewModel() {
    var mIsSnsJoin = false

    // email
    var mTimerMinute = ObservableField("00")
        @Bindable
        get() = field
    var mTimerSecond = ObservableField("00")
        @Bindable
        get() = field
    var mEmailVerifyVisible = ObservableBoolean(false)
        @Bindable
        get() = field
    var mEmailVerifyBtnText = ObservableField(BaseApplication.getInstance().getString(R.string.luckydraw_checkduplicate))
        @Bindable
        get() = field
    var mEmail = ""
        set(value) {
            field = value

            if (value.isNotEmpty()) resetEmailVerify()
        }
    var mIsEmailDuplicate = ObservableBoolean(false)
        @Bindable
        get() = field
    var mIsEmailVerified = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            mNotifyJoinAvailableTask()
        }

    var mEmailVerifyNumber = ""
        set(value) {
            if (value.isNotEmpty())
                field = value
        }
    var mResetVerifyNumberTask: () -> Unit = {}

    // password
    var mPassword = ""
        set(value) {
            field = value
            mIsPasswordVerified = ObservableBoolean(CommonUtil.validatePassword(value))
            notifyPropertyChanged(BR.mIsPasswordVerified)

            if (mConfirmPassword.isNotEmpty()) {
                mIsPasswordConfirmVerified = ObservableBoolean(mPassword == mConfirmPassword)
                notifyPropertyChanged(BR.mIsPasswordConfirmVerified)
            }
        }
    var mConfirmPassword = ""
        set(value) {
            field = value
            mIsPasswordConfirmVerified = ObservableBoolean(mPassword == value)
            notifyPropertyChanged(BR.mIsPasswordConfirmVerified)
        }
    var mIsPasswordVerified = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            mNotifyJoinAvailableTask()
        }
    var mIsPasswordConfirmVerified = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            mNotifyJoinAvailableTask()
        }

    // verify mobile
    var mVerifyMobileTask: () -> Unit = {}
    var mDiCode = ""
    var mBirth = ""
    var mGender = ""
    var mName = ObservableField(BaseApplication.getInstance().getString(R.string.mypage_userinfo_passwaord_infotitle2))
        @Bindable
        get() = field
    var mMobile = ObservableField(BaseApplication.getInstance().getString(R.string.luckydraw_hint_mobile))
        @Bindable
        get() = field
    var mIsMobileVerified = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            mNotifyJoinAvailableTask()
        }

    // join
    var mIsTermsAllChecked = false
    var mIsJoinAvailable = ObservableBoolean(false)
        @Bindable
        get() = field
    var mNotifyJoinAvailableTask: () -> Unit = {}

    fun onClickVerifyEmail() {
        if (!mIsEmailVerified.get()) {
            if (!mIsEmailDuplicate.get()) {
                checkDuplicateEmail()
            } else {

                if (CountTimer.isResendable()) {
                    sendEmailVerifyNumber(successTask = { o ->
                        mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_resend))
                        mEmailVerifyVisible.set(true)
                        notifyPropertyChanged(BR.mEmailVerifyVisible)

                        val result = (o.data as LinkedTreeMap<*, *>)["data"]
                        val second =
                                if (result != null) Math.round((result as Double?)!!).toInt()
                                else 180000
                        val minute = second / 60000

                        CountTimer.startVerifyNumberTimer("00", minute.toString(), object : OnTimerListener {
                            override fun changeSecond(second: String?) {
                                mTimerSecond.set(second ?: "00")
                            }

                            override fun changeMinute(minute: String?) {
                                mTimerMinute.set(minute ?: "00")
                            }

                            override fun notifyMinuteAndSecond() {
                                notifyPropertyChanged(BR.timerMinute)
                                notifyPropertyChanged(BR.timerSecond)
                            }
                        })
                    })
                }
            }
        }
    }

    fun onClickVerifyNumber() {
        val verification = Verification()
        verification.verificationNumber = mEmailVerifyNumber
        verification.verificationTargetType = VerificationType.EMAIL.type
        verification.verificationTarget = mEmail
        verifyNumber(verification, successTask = {
            CountTimer.stopTimer()
            mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_finishverify))
            mResetVerifyNumberTask()

            mIsEmailVerified.set(true)
            notifyPropertyChanged(BR.mIsEmailVerified)

            mEmailVerifyVisible.set(false)
        })
    }

    private fun checkDuplicateEmail() {
        if (mEmail.isNotEmpty())
            UserServer.checkEmail(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mIsEmailDuplicate.set(false)
                            notifyPropertyChanged(BR.mIsEmailDuplicate)

//                            mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_verifyemail))
//                            mEmailErrorMessage.set(BaseApplication.getInstance().getString(R.string.payment_message_verifyemail))

                            /**
                             * [19.11.14] 이메일 인증 제외로 결정 (cto님 피셜)
                             * @author Hyeyeon Park
                             */
                            mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_finishcheck))

                            mResetVerifyNumberTask()

                            mIsEmailVerified.set(true)
                            notifyPropertyChanged(BR.mIsEmailVerified)

                        },
                        alreadyExistEmailTask = {
                            // 중복 이메일
                            val message = if (o is BaseModel<*>) o.message else BaseApplication.getInstance().getString(R.string.common_message_servererror)
                            ToastUtil.showMessage(message)

                            mIsEmailDuplicate.set(true)
                            notifyPropertyChanged(BR.mIsEmailDuplicate)

                            mEmailVerifyVisible.set(false)
                            notifyPropertyChanged(BR.mEmailVerifyVisible)

//                            mEmailErrorMessage.set(BaseApplication.getInstance().getString(R.string.mypage_userinfo_hint_errornickname))
                        })
            }, email = mEmail)
    }

    private fun sendEmailVerifyNumber(successTask: (BaseModel<*>) -> Unit) {
        val user = User().apply {
            this.email = mEmail
            this.name = null
        }

        UserServer.verifyEmail(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = { successTask(o as BaseModel<*>) },
                    dataNotFoundTask = { ToastUtil.showMessage((o as BaseModel<*>).message) })
        }, user = user)
    }

    private fun verifyNumber(verification: Verification, successTask: () -> Unit) {
        UserServer.verifyNumber(OnServerListener { success, o ->
            val model = o as BaseModel<*>
            if (success)
                if (model.resultCode == ResultCode.SUCCESS.flag) successTask()
                else ToastUtil.showMessage(model.message)
            else
                ToastUtil.showMessage(model.message)
        }, verification)
    }

    private fun resetEmailVerify() {
        mIsEmailDuplicate.set(false)
        mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_checkduplicate))

        if (mIsEmailVerified.get()) mIsEmailVerified = ObservableBoolean(false)
        if (mEmailVerifyVisible.get()) {
            mEmailVerifyVisible.set(false)
            notifyPropertyChanged(BR.mEmailVerifyVisible)
        }

        if (mTimerMinute.get() != "00" || mTimerSecond.get() != "00") {
            CountTimer.stopTimer()
            mTimerMinute.set("00")
            mTimerSecond.set("00")
            notifyPropertyChanged(BR.mTimerMinute)
            notifyPropertyChanged(BR.mTimerSecond)
        }
    }

    fun onClickVerifyMobile() = mVerifyMobileTask()

    fun signUpEventUser(agreeCollectPersonalInfoTos: Boolean, agreePurchaseTos: Boolean, agreeSaleTos: Boolean, agreeEmailReception: Boolean, agreeSmsReception: Boolean) {
        val eventUser = EventUser().apply {
            this.identityVerify = IdentityVerify().apply {
                this.birth = mBirth
                this.diCode = mDiCode
                this.gender = mGender
                this.identityVerifyMethod = Verification.IdentityVerifyMethod.MOBILE.code
                this.mobile = mMobile.get() ?: ""
                this.name = mName.get() ?: ""
            }

            this.userSignUp = UserSignUp().apply {
                this.agreeCollectPersonalInfoTos = agreeCollectPersonalInfoTos
                this.agreePurchaseTos = agreePurchaseTos
                this.agreeSaleTos = agreeSaleTos
                this.agreeEmailReception = agreeEmailReception
                this.agreeSmsReception = agreeSmsReception
                this.email = mEmail
                this.password = mPassword
            }

            this.verification = Verification().apply {
                this.verificationNumber = mEmailVerifyNumber.toInt()
                this.verificationTarget = mEmail
                this.verificationTargetType = VerificationType.EMAIL.type
            }
        }

        UserServer.signUpEventUser(OnServerListener { success, o ->
            if (success && o is BaseModel<*>)
                if (o.resultCode == ResultCode.SUCCESS.flag) {
                    ToastUtil.showMessage("럭키드로우 회원가입 완료")
                    if (CustomLog.flag) CustomLog.L("럭키드로우 이메일 회원가입", mEmail)


                    /// 응모하기 api 예정
                } else
                    ToastUtil.showMessage(o.message)
            else
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
        }, eventUser = eventUser)
    }
}