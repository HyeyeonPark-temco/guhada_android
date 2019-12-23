package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import io.temco.guhada.BR
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class DetailSearchDialogViewModel (val context : Context) : BaseObservableViewModel() {


    var detailSearchDialogShipInfo1 = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.detailSearchDialogShipInfo1)
        }

    var detailSearchDialogShipInfo2 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.detailSearchDialogShipInfo2)
        }
    var detailSearchDialogPdtCdt1 = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.detailSearchDialogPdtCdt1)
        }
    var detailSearchDialogPdtCdt2 = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.detailSearchDialogPdtCdt2)
        }
}