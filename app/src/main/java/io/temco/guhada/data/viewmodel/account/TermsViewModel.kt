package io.temco.guhada.data.viewmodel.account

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import io.temco.guhada.BR
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class TermsViewModel : BaseObservableViewModel() {
    var user = User()
    var mCloseTask: (resultCode: Int) -> Unit = {}

    val allChecked = ObservableBoolean(false)
        @Bindable
        get() = field
    var essentialChecked = ObservableBoolean(false)
        @Bindable
        get() = field
    var optionalChecked = ObservableBoolean(false)
        @Bindable
        get() = field

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

    fun onCheckPurchaseTos(checked: Boolean) {
        user.agreePurchaseTos = checked
        checkEssentialTermsAllChecked()
    }

    fun onCheckPrivacyTos(checked: Boolean) {
        user.agreeCollectPersonalInfoTos = checked
        checkEssentialTermsAllChecked()
    }

    fun onCheckSaleTos(checked: Boolean) {
        user.agreeSaleTos = checked
        checkOptionalTermsAllChecked()
    }

    fun onCheckEmailReception(checked: Boolean) {
        user.agreeEmailReception = checked
        checkOptionalTermsAllChecked()
    }

    fun onCheckSmsReception(checked: Boolean) {
        user.agreeSmsReception = checked
        checkOptionalTermsAllChecked()
    }

    // CLICK LISTENER
    fun onClickSignUp() = mCloseTask(RESULT_OK)
    fun onClickBack()  = mCloseTask(RESULT_CANCELED)

    private fun setEssentialTerms(checked: Boolean) {
        user.agreePurchaseTos = checked
        user.agreeCollectPersonalInfoTos = checked
        checkEssentialTermsAllChecked()
    }

    private fun setOptionalTerms(checked: Boolean) {
        user.agreeSaleTos = checked
        user.agreeEmailReception = checked
        user.agreeSmsReception = checked
        checkOptionalTermsAllChecked()
    }

    private fun checkEssentialTermsAllChecked() {
        val personalInfoTosChecked = user.agreeCollectPersonalInfoTos!!
        val purchaseTosChecked = user.agreePurchaseTos!!
        val isEssentialAllChecked = personalInfoTosChecked && purchaseTosChecked

        if (essentialChecked.get() != isEssentialAllChecked) {
            essentialChecked.set(isEssentialAllChecked)
            notifyPropertyChanged(BR.essentialChecked)
        }
    }

    private fun checkOptionalTermsAllChecked() {
        val saleTosChecked = user.agreeSaleTos!!
        val emailReceptionChecked = user.agreeEmailReception!!
        val smsReceptionChecked = user.agreeSmsReception!!
        val isOptionAllChecked = saleTosChecked && emailReceptionChecked && smsReceptionChecked

        if (optionalChecked.get() != isOptionAllChecked) {
            optionalChecked.set(isOptionAllChecked)
            notifyPropertyChanged(BR.optionalChecked)
        }
    }
}