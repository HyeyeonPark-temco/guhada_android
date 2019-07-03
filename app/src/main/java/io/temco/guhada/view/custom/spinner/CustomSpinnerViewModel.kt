package io.temco.guhada.view.custom.spinner

import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import io.temco.guhada.BR
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CustomSpinnerViewModel(val listener: OnCustomSpinnerListener) : BaseObservableViewModel() {
    var isDropdownShown = false
    var list: List<String> = ArrayList()
    var selectedItem: ObservableField<String> = ObservableField("")
        @Bindable
        get() = field

    fun onClickContainer() {
        listener.closeSpinner()
        isDropdownShown = !isDropdownShown
    }

    fun onItemSelected(pos: Int) {
        isDropdownShown = false
        selectedItem = ObservableField(list[pos])
        notifyPropertyChanged(BR.selectedItem)
    }
}