package io.temco.guhada.data.model

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import io.temco.guhada.BR
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class AppSettingModel(val context : Context) : BaseObservableViewModel(){

    var version = "1.0.17"

    var appSettingFlag01 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.appSettingFlag01)
        }
    var appSettingFlag02 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.appSettingFlag02)
        }
    var appSettingFlag03 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.appSettingFlag03)
        }
    var appSettingFlag04 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.appSettingFlag04)
        }
    var appSettingFlag05 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.appSettingFlag05)
        }
    var appSettingFlag06 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.appSettingFlag06)
        }
    var appSettingFlagVersionCheck = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.appSettingFlagVersionCheck)
        }
}