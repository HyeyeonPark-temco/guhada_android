package io.temco.guhada.data.viewmodel.shippingaddress

import android.app.Activity.RESULT_OK
import com.auth0.android.jwt.JWT
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.server.UserServer


class AddShippingAddressViewModel(mListener: OnShippingAddressListener) : ShippingAddressViewModel(mListener) {
    fun onClickSubmit() {
        checkEmptyField {
            if (Preferences.getToken() != null) {
                val accessToken = Preferences.getToken().accessToken
                if (accessToken != null) {
                    val userId = JWT(accessToken).getClaim("userId").asInt()
                    if (userId != null) {
                        newItem.userId = userId
                        saveShippingAddress(userId)
                    } else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                } else {
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                }
            } else {
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            }
        }
    }

    private fun saveShippingAddress(userId: Int) {
        UserServer.saveUserShippingAddress(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_add_success))
                        mListener.closeActivity(RESULT_OK, newItem)
                    })
        }, userId, newItem)
    }
}