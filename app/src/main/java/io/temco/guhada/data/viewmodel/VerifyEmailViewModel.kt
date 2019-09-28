package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.google.gson.internal.LinkedTreeMap
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnTimerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CountTimer
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Verification
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class VerifyEmailViewModel : BaseObservableViewModel() {
    var mOnSuccessVerify: () -> Unit = {}
    var mVerifyEmailVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var mTimerSecond = ObservableField("60")
        @Bindable
        get() = field
    var mTimerMinute = ObservableField("02")
        @Bindable
        get() = field
    var mEmail = ObservableField("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mEmail)
        }
    var mName = ObservableField("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mName)
        }
    var mVerifyNumber = ObservableField("")
        @Bindable
        get() = field

    /**
     * 이메일로 인증번호 발송
     * @author Hyeyeon Park
     * @since 2019.09.28
     */
    fun onClickSendEmail() {
        if (CommonUtil.validateEmail(mEmail.get())) {
            User().apply {
                this.email = mEmail.get()
                this.name = mName.get()
            }.let { user ->
                UserServer.verifyEmail(OnServerListener { success, o ->
                    if (success) {
                        val model = o as BaseModel<*>
                        when (model.resultCode) {
                            200 -> {
                                mVerifyEmailVisibility = ObservableInt(View.VISIBLE)
                                notifyPropertyChanged(BR.mVerifyEmailVisibility)

                                val second = java.lang.Double.parseDouble((o.data as LinkedTreeMap<*, *>)["data"]!!.toString())
                                val minute = (second / 60000).toInt()
                                if (minute.toString().length == 1) {
                                    mTimerSecond = ObservableField("60")
                                    mTimerMinute = ObservableField("0${minute - 1}")
                                    startTimer()
                                } else {
                                    mTimerSecond = ObservableField("60")
                                    mTimerMinute = ObservableField("${minute - 1}")
                                    startTimer()
                                }
                            }
                            6005 -> ToastUtil.showMessage(BaseApplication.getInstance().resources.getString(R.string.findpwd_message_wronginfo))
                        }
                    } else {
                        val message = o as String
                        ToastUtil.showMessage((message))
                    }
                }, user)
            }
        } else {
            ToastUtil.showMessage((BaseApplication.getInstance().resources.getString(R.string.findpwd_message_invalidemailformat)))
        }
    }

    /**
     * 인증번호 검증
     */
    fun onClickVerifyNumber() {
        val verification = Verification()
        verification.verificationNumber = mVerifyNumber.get() ?: ""
        verification.verificationTarget = mEmail.get() ?: ""
        verification.verificationTargetType = Verification.IdentityVerifyMethod.EMAIL.code

        UserServer.verifyNumber(OnServerListener { success, o ->
            if (success) {
                val model = o as BaseModel<*>
                when (model.resultCode) {
                    Flag.ResultCode.SUCCESS -> {
                        mOnSuccessVerify()
                    }
                    Flag.ResultCode.EXPIRED_VERIFICATION_NUMBER -> {
                        ToastUtil.showMessage((BaseApplication.getInstance().resources.getString(R.string.findpwd_message_expiredverification)))
                    }
                    Flag.ResultCode.INVALID_VERIFICATION_NUMBER -> {
                        ToastUtil.showMessage((BaseApplication.getInstance().resources.getString(R.string.findpwd_message_invaludverification)))
                    }
                    else -> {
                        ToastUtil.showMessage(model.message)
                    }
                }
            } else {
                val message = o as String
                ToastUtil.showMessage(message)
            }

        }, verification)
    }

    private fun startTimer() {
        CountTimer.startVerifyNumberTimer(mTimerSecond.get(), mTimerMinute.get(), object : OnTimerListener {
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
}