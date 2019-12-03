package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.coupon.Coupon
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
    private val UNIT_PER_PAGE = 4
    var page = 1

    var enabledCouponResponse: MutableLiveData<CouponResponse> = MutableLiveData()
        @Bindable
        get() = field
    var disabledCouponResponse: MutableLiveData<CouponResponse> = MutableLiveData()
        @Bindable
        get() = field

    fun getCoupons(isAvailable: Boolean) {
        ServerCallbackUtil.callWithToken(task = { token ->
            BenefitServer.getCoupons(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val response = (it.data as CouponResponse)
                            if (isAvailable) this.enabledCouponResponse.postValue(response)
                            else this.disabledCouponResponse.postValue(response)
                        })
            }, accessToken = token, isAvailable = isAvailable, page = page++, unitPerPage = UNIT_PER_PAGE)
        })
    }

    fun onClickMore(isAvailable: Boolean) {
//        page++
        getCoupons(isAvailable)
    }
}