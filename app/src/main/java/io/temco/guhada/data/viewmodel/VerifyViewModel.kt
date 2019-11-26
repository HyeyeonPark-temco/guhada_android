package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.JsonObject
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

class VerifyViewModel : BaseObservableViewModel() {
    private val START_MINUTE = "02"
    private val START_SECOND = "60"

    var mVerificationNumber = "" // 인증번호
    var mUser = User()
        set(value) {
            field = value
            mEmailVerification = ObservableBoolean(value.emailVerify)
            mVerifyEmail = if (CommonUtil.validateEmail(mUser.email)) ObservableField(value.email) else ObservableField("")
        }

    var mVerification = Verification()

    var mMobileVerification = ObservableBoolean(false)
        @Bindable
        get() = field

    var mEmailVerification = ObservableBoolean(false)
        @Bindable
        get() = field

    var mTimerMinute = ObservableField(START_MINUTE)
        @Bindable
        get() = field

    var mTimerSecond = ObservableField(START_SECOND)
        @Bindable
        get() = field

    var mActiveSendButton = ObservableBoolean(false)
        @Bindable
        get() = field

    var mVerifyPhoneTask: () -> Unit = {}

    var mVerifyEmail = ObservableField("")
        @Bindable
        get() = field


    // 이메일로 인증번호 전송
    fun onClickSend() {
        if (!mActiveSendButton.get()) {
            if (!mMobileVerification.get()) {
                // 본인인증을 안하면 name 필드 값이 없음 (email 인증번호 발송 api에서 name이 필수 값)
                ToastUtil.showMessage("휴대폰 본인인증을 먼저 진행해주세요.")
            } else {
                sendNumber()
            }
        } else {
            verifyNumber()
        }
    }

    // 이메일로 인증번호 재전송
    fun onClickResend() = sendNumber()

    // 휴대폰 본인인증
    fun onClickVerifyPhone() = mVerifyPhoneTask()

    private fun startTimer(minute: String, second: String) {
        CountTimer.startVerifyNumberTimer(second, minute, object : OnTimerListener {
            override fun changeSecond(second: String) {
                mTimerSecond = ObservableField(second)
            }

            override fun changeMinute(minute: String) {
                mTimerMinute = ObservableField(minute)
            }

            override fun notifyMinuteAndSecond() {
                notifyPropertyChanged(BR.mTimerSecond)
                notifyPropertyChanged(BR.mTimerMinute)
            }
        })
    }

    // 이메일로 인증번호 전송
    private fun sendNumber() {
        mUser.email = mVerifyEmail.get()
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            UserServer.verifyEmail(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.verify_message_send))
                            mActiveSendButton = ObservableBoolean(true)
                            notifyPropertyChanged(BR.mActiveSendButton)
                            startTimer(minute = START_MINUTE, second = START_SECOND)
                        },
                        wrongInfoTask = {
                            ToastUtil.showMessage(it.message)
                        })
            }, user = mUser)
        })
    }

    // TODO 인증 완료 화면 미정 [2019.09.05]
    private fun verifyNumber() {
        UserServer.verifyNumber(OnServerListener { success, o ->
            if (success && o is BaseModel<*>) {
                if (o.resultCode == ResultCode.SUCCESS.flag) {
                    CountTimer.stopTimer()
                    updateEmailVerify()
                } else {
                    ToastUtil.showMessage(o.message)
                }
            } else {
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
            }
        }, verification = Verification().apply {
            this.verificationTarget = mVerifyEmail.get() ?: "" //mUser.email
            this.verificationTargetType = VerificationType.EMAIL.type
            this.verificationNumber = mVerificationNumber
        })
    }

    private fun updateEmailVerify() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("verificationNumber", mVerificationNumber)
        jsonObject.addProperty("email", mVerifyEmail.get())

        ServerCallbackUtil.callWithToken(task = { accessToken ->
            UserServer.updateEmailVerify(OnServerListener { success, o ->
                val model = o as BaseModel<*>
                if (success && model.resultCode == ResultCode.SUCCESS.flag) {
                    mEmailVerification = ObservableBoolean(true)
                    notifyPropertyChanged(BR.mEmailVerification)
                    ToastUtil.showMessage("이메일 인증 완료")
                } else {
                    ToastUtil.showMessage(model.message)
                }
            }, accessToken = accessToken, jsonObject = jsonObject)
        })
    }

    // 본인인증 정보 유저 정보로 업데이트
    fun updateIdentityVerify() {
        ServerCallbackUtil.callWithToken(task = {
            UserServer.updateIdentityVerify(OnServerListener { success, o ->
                val resultCode = (o as BaseModel<*>).resultCode
                if (resultCode == ResultCode.SUCCESS.flag) {
                    mMobileVerification = ObservableBoolean(true)
                    notifyPropertyChanged(BR.mMobileVerification)
                    ToastUtil.showMessage("본인 인증 완료")
                } else {
                    ToastUtil.showMessage(o.message ?: "유저 정보 업데이트 오류")
                }
            }, accessToken = it, verification = mVerification)
        })
    }

    fun getEmailValidation(): Boolean = CommonUtil.validateEmail(mVerifyEmail.get()) //mUser.email)

}