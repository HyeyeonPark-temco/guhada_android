package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.VerificationType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnTimerListener
import io.temco.guhada.common.util.CountTimer
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Verification
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class VerifyViewModel : BaseObservableViewModel() {
    private val START_MINUTE = "02"
    private val START_SECOND = "60"

    var mEmail = ""
    var mName = ""
    var mVerificationNumber = ""

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

    // 이메일로 인증번호 전송
    fun onClickSend() {
        if (!mActiveSendButton.get()) {
            sendNumber()
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
        }, user = User().apply {
            this.name = mName
            this.email = mEmail
        })
    }

    // TODO 인증 완료 화면 미정 [2019.09.05]
    private fun verifyNumber() {
        UserServer.verifyNumber(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        CountTimer.stopTimer()
                        mEmailVerification = ObservableBoolean(true)
                        notifyPropertyChanged(BR.mEmailVerification)
                        ToastUtil.showMessage("이메일 인증 완료")
                    },
                    invalidVerificationNumberTask = {
                        ToastUtil.showMessage(it.message)
                    })
        }, verification = Verification().apply {
            this.verificationTarget = mEmail
            this.verificationTargetType = VerificationType.EMAIL.type
            this.verificationNumber = mVerificationNumber
        })
    }

}