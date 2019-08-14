package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.point.PointSummary
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ReviewPointDialogViewModel (val context : Context) : BaseObservableViewModel() {
    var pointSummary: ObservableField<PointSummary> = ObservableField()
        @Bindable
        get() = field

    /**
     * 0 : 구매확정, 1 : 리뷰작성 완료, 2 : 사이즈 등록 완료
     */
    var mTypeNotPresentException = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mTypeNotPresentException)
        }


    fun getPointSummary() {
        ServerCallbackUtil.callWithToken(task = { token ->
            BenefitServer.getPointSummary(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            this.pointSummary = ObservableField(it.data as PointSummary)
                            notifyPropertyChanged(BR.pointSummary)
                        })
            }, token, expireDays = 30)
        })
    }




}