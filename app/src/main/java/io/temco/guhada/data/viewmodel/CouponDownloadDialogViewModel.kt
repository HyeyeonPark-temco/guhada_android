package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CouponDownloadDialogViewModel : BaseObservableViewModel() {
    var mList: MutableList<Coupon> = mutableListOf()
    var mIsFollow = ObservableBoolean(false)
        @Bindable
        get() = field
    var mOnClickCloseTask: () -> Unit = {}

    fun onClickClose() = mOnClickCloseTask()
    fun onClickDownload() {

    }
}