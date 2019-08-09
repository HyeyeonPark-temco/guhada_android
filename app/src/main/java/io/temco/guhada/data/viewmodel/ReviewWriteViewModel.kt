package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ReviewWriteViewModel (val context : Context) : BaseObservableViewModel() {


    var modifyReviewStatus = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.modifyReviewStatus)
        }


    var reviewSelectStatus1 = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.reviewSelectStatus1)
        }

    var reviewSelectStatus2 = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.reviewSelectStatus2)
        }

    var reviewSelectStatus3 = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.reviewSelectStatus3)
        }


    fun clickReviewSelectStatus1(select : Int){
        reviewSelectStatus1.set(select)
    }

    fun clickReviewSelectStatus2(select : Int){
        reviewSelectStatus2.set(select)
    }

    fun clickReviewSelectStatus3(select : Int){
        reviewSelectStatus3.set(select)
    }

}