package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class LuckyDrawJoinViewModel : BaseObservableViewModel() {
    var timerMinute = ObservableField("00")
        @Bindable
        get() = field
    var timerSecond = ObservableField("00")
        @Bindable
        get() = field
}