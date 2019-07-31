package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.content.Intent
import com.auth0.android.jwt.JWT
import io.temco.guhada.R
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.AddShippingAddressActivity
import io.temco.guhada.view.activity.MainActivity


/**
 * @author park jungho
 * 19.07.22
 *
 * 마이페이지 Fragment ViewModel
 *
 */
class MyPageViewModel(val context: Context) : BaseObservableViewModel() {
    fun onClickDefault(selectedPos: Int, shippingAddress: UserShipping?, successTask: () -> Unit) {
        val token = Preferences.getToken()
        if (token != null) {
            val accessToken = token.accessToken
            if (accessToken != null) {
                if (selectedPos > 0) {
                    if (shippingAddress != null) {
                        val userId = JWT(accessToken).getClaim("userId").asInt()
                        if (userId != null) {
                            shippingAddress.defaultAddress = true
                            UserServer.updateUserShippingAddress(OnServerListener { success, o ->
                                ServerCallbackUtil.executeByResultCode(success, o,
                                        successTask = {
                                            val data = EventBusData(RequestCode.ADD_SHIPPING_ADDRESS.flag, null)
                                            EventBusHelper.sendEvent(data)
                                        })
                            }, userId = userId, shippingAddressId = shippingAddress.id, shippingAddress = shippingAddress)
                        }
                    }
                } else {
                    ToastUtil.showMessage("배송지를 선택해주세요.")
                }
            }

        } else
            ToastUtil.showMessage(context.getString(R.string.login_message_requiredlogin))
    }
}