package io.temco.guhada.data.viewmodel

import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CouponDownloadDialogViewModel : BaseObservableViewModel() {
    var mList: MutableList<Coupon> = mutableListOf()
    var mOnClickCloseTask: () -> Unit = {}

    fun onClickClose() = mOnClickCloseTask()
    fun onClickDownload() {

    }
}