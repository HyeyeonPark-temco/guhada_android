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
import io.temco.guhada.data.model.TempUserShipping
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
                    }
                    else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                } else {
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                }
            }else {
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            }
        }
    }

    private fun saveShippingAddress(userId: Int) {
        // [2019.07.25] 임시
        val tempUserShipping = TempUserShipping().apply {
            this.addList = newItem.addList
            this.address = newItem.address
            this.defaultAddress = newItem.defaultAddress
            this.detailAddress = newItem.detailAddress
            this.id = newItem.id
            this.recipientMobile = newItem.recipientMobile
            this.recipientName = newItem.recipientName
            this.roadAddress = newItem.roadAddress
            this.safetyMobile = newItem.safetyMobile
            this.shippingName = newItem.shippingName
            this.userId = newItem.userId
            this.zip = newItem.zip

            this.shippingMessage = ""
            this.shippingMessageType = ""
        }

        UserServer.tempSaveUserShippingAddress(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_add_success))
                        mListener.closeActivity(RESULT_OK, newItem)
                    })
        }, userId, tempUserShipping)
    }
}