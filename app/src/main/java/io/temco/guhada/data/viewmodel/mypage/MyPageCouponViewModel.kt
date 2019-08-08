package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.coupon.CouponResponse
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 쿠폰
 * @author Hyeyeon Park
 * @since 2019.08.08
 *
 */
class MyPageCouponViewModel(val context: Context) : BaseObservableViewModel() {
    var couponResponse: ObservableField<CouponResponse> = ObservableField()
        @Bindable
        get() = field
    var page = 1
    private val UNIT_PER_PAGE = 4
    var isAvailable = true

    fun getCoupons() {
        ServerCallbackUtil.callWithToken(task = { token ->
            BenefitServer.getCoupons(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            this.couponResponse = ObservableField(it.data as CouponResponse)
                            notifyPropertyChanged(BR.couponResponse)
                        })
            }, accessToken = token, isAvailable = isAvailable, page = page, unitPerPage = UNIT_PER_PAGE)

        })
    }

}