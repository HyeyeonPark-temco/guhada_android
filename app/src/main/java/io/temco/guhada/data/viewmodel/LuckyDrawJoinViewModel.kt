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
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CountTimer
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Verification
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 럭키드로우 회원가입 ViewModel
 * @author Hyeyeon Park
 */
class LuckyDrawJoinViewModel : BaseObservableViewModel() {

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
    var mIsEmailDuplicate: Boolean? = null
    var mIsEmailVerified = ObservableBoolean(false)
        @Bindable
        get() = field
    var mEmailVerifyNumber = ""

    // password
    var mPassword = ""
        set(value) {
            field = value
            mIsPasswordVerified.set(CommonUtil.validatePassword(value))
            notifyPropertyChanged(BR.mIsPasswordVerified)

            if (mConfirmPassword.isNotEmpty()) {
                mIsPasswordConfirmVerified.set(mPassword == mConfirmPassword)
                notifyPropertyChanged(BR.mIsPasswordConfirmVerified)
            }
        }
    var mConfirmPassword = ""
        set(value) {
            field = value
            mIsPasswordConfirmVerified.set(mPassword == value)
            notifyPropertyChanged(BR.mIsPasswordConfirmVerified)
        }
    var mIsPasswordVerified = ObservableBoolean(false)
        @Bindable
        get() = field
    var mIsPasswordConfirmVerified = ObservableBoolean(false)
        @Bindable
        get() = field

    // verify mobile
    var mVerifyMobileTask: () -> Unit = {}
    var mDiCode = ""
    var mName = ObservableField(BaseApplication.getInstance().getString(R.string.mypage_userinfo_passwaord_infotitle2))
        @Bindable
        get() = field
    var mMobile = ObservableField(BaseApplication.getInstance().getString(R.string.luckydraw_hint_mobile))
        @Bindable
        get() = field

    fun onClickVerifyEmail() {
        if (!mIsEmailVerified.get()) {
            if (mIsEmailDuplicate == null || mIsEmailDuplicate == true) {
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
            mEmailVerifyNumber = ""
            mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_finishverify))

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
                            mIsEmailDuplicate = false
                            mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_verifyemail))
                        },
                        alreadyExistEmailTask = {
                            // 중복 이메일
                            val message = if (o is BaseModel<*>) o.message else BaseApplication.getInstance().getString(R.string.common_message_servererror)
                            ToastUtil.showMessage(message)

                            mIsEmailDuplicate = true
                            mEmailVerifyVisible.set(false)
                            notifyPropertyChanged(BR.mEmailVerifyVisible)
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
        mIsEmailDuplicate = null
        mEmailVerifyBtnText.set(BaseApplication.getInstance().getString(R.string.luckydraw_checkduplicate))

        mIsEmailVerified = ObservableBoolean(false)
//        notifyPropertyChanged(BR.mIsEmailVerified)

        mEmailVerifyVisible.set(false)
        notifyPropertyChanged(BR.mEmailVerifyVisible)

        CountTimer.stopTimer()
        mTimerMinute.set("00")
        mTimerSecond.set("00")
        notifyPropertyChanged(BR.mTimerMinute)
        notifyPropertyChanged(BR.mTimerSecond)
    }


    fun onClickVerifyMobile() = mVerifyMobileTask()
}