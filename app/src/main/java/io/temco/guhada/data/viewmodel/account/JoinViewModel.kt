package io.temco.guhada.data.viewmodel.account

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnJoinListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import java.util.*

class JoinViewModel : BaseObservableViewModel(), Observer {
    lateinit var listener: OnJoinListener
    var toolBarTitle = ""
    var user = User()

    // TERMS CHECKED
    var allChecked = ObservableBoolean(false)
        @Bindable
        get() = field
    var essentialChecked = ObservableBoolean(false)
        @Bindable
        get() = field
    var optionalChecked = ObservableBoolean(false)
        @Bindable
        get() = field
    val selectTermPlusView = ObservableBoolean(false)
        @Bindable
        get() = field

    init {
        this.user.addObserver(this)
    }

    // CLICK LISTENER
    fun onClickBack() = listener.closeActivity(RESULT_CANCELED)

    fun onClickSignUp() {
        if (!CommonUtil.validateEmail(user.email))
            listener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.join_message_wrongidformat))
        else if (user.password.length < 8 || user.password.length > 15)
            listener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.join_message_wrondpwdlength))
        else if (!CommonUtil.validatePassword(user.password))
            listener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.join_message_wrongpwdformat))
        else if (user.confirmPassword != user.password)
            listener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.join_message_notequalpwd))
        else signUp()
    }

    private fun signUp() {
        UserServer.signUp(OnServerListener { success, o ->
            if (success) {
                val model = o as BaseModel<*>
                when (model.resultCode) {
                    ResultCode.SUCCESS.flag -> {
                        user.deleteObserver(this)
                        user.email = if (model.data is String) model.data as String else user.email
                        listener.closeActivity(RESULT_OK)
                    }
                    ResultCode.ALREADY_EXIST_EMAIL.flag -> listener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.join_message_existemail))
                    ResultCode.SIGNUP_INVALID_PASSWORD.flag -> listener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.join_message_wrongpwdformat))
                }
            } else {
                listener.showSnackBar(o as String)
            }
        }, user)
    }

    // TERMS CHECK LISTENER
    fun onCheckAll(checked: Boolean) {
        if (checked) {
            setEssentialTerms(true)
            setOptionalTerms(true)
        } else if (essentialChecked.get() && optionalChecked.get()) {
            setEssentialTerms(false)
            setOptionalTerms(false)
        }
    }

    fun onCheckEssential(checked: Boolean) {
        essentialChecked = ObservableBoolean(checked)
        notifyPropertyChanged(BR.essentialChecked)

        if (checked)
            setEssentialTerms(true)
        else if (user.agreePurchaseTos!! && user.agreeCollectPersonalInfoTos!!)
            setEssentialTerms(false)
    }

    fun onCheckOption(checked: Boolean) {
        optionalChecked = ObservableBoolean(checked)
        notifyPropertyChanged(BR.optionalChecked)

        if (checked)
            setOptionalTerms(true)
        else if (user.agreeSaleTos!! && user.agreeEmailReception!! && user.agreeSmsReception!!)
            setOptionalTerms(false)
    }

    private fun setEssentialTerms(checked: Boolean) {
        user.agreePurchaseTos = checked
        user.agreeCollectPersonalInfoTos = checked
    }

    private fun setOptionalTerms(checked: Boolean) {
        user.agreeSaleTos = checked
        user.agreeEmailReception = checked
        user.agreeSmsReception = checked
    }

    fun onClickSelectTermPlusView() {
        this.selectTermPlusView.set(true)
        notifyPropertyChanged(BR.selectTermPlusView)
    }

    fun onCheckPurchaseTos(checked: Boolean) {
        user.agreePurchaseTos = checked
    }

    fun onCheckPrivacyTos(checked: Boolean) {
        user.agreeCollectPersonalInfoTos = checked
    }

    fun onCheckSaleTos(checked: Boolean) {
        user.agreeSaleTos = checked
    }

    fun onCheckEmailReception(checked: Boolean) {
        user.agreeEmailReception = checked
    }

    fun onCheckSmsReception(checked: Boolean) {
        user.agreeSmsReception = checked
    }

    override fun update(o: Observable?, arg: Any?) {
        if (arg is String) {
            when (arg) {
                // ESSENTIAL TERMS
                "agreeCollectPersonalInfoTos", "agreePurchaseTos" -> {
                    val personalInfoTosChecked = user.agreeCollectPersonalInfoTos!!
                    val purchaseTosChecked = user.agreePurchaseTos!!
                    val isEssentialAllChecked = personalInfoTosChecked && purchaseTosChecked

                    if (essentialChecked.get() != isEssentialAllChecked) {
                        essentialChecked = ObservableBoolean(isEssentialAllChecked)
                        notifyPropertyChanged(BR.essentialChecked)
                    }

                    if (CustomLog.flag) CustomLog.L("update", "isEssentialAllChecked", isEssentialAllChecked)
                    if (CustomLog.flag) CustomLog.L("update", "essentialChecked.get() ", essentialChecked.get())
                }

                "agreeSaleTos", "agreeEmailReception", "agreeSmsReception" -> {
                    val saleTosChecked = user.agreeSaleTos!!
                    val emailReceptionChecked = user.agreeEmailReception!!
                    val smsReceptionChecked = user.agreeSmsReception!!
                    val isOptionAllChecked = saleTosChecked && emailReceptionChecked && smsReceptionChecked

                    if (optionalChecked.get() != isOptionAllChecked) {
                        optionalChecked = ObservableBoolean(isOptionAllChecked)
                        notifyPropertyChanged(BR.optionalChecked)
                    }
                }
            }
        }
    }
}