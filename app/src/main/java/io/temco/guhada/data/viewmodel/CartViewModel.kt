package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import io.temco.guhada.BR
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CartViewModel : BaseObservableViewModel() {
    var totalDiscountVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    fun onClickDiscountContent() {
        totalDiscountVisible = ObservableBoolean(!totalDiscountVisible.get())
        notifyPropertyChanged(BR.totalDiscountVisible)
    }
}