package io.temco.guhada.data.viewmodel

import android.app.Activity
import androidx.databinding.Bindable
import com.auth0.android.jwt.JWT
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnEditShippingAddressListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class EditShippingAddressViewModel(val mListener: OnEditShippingAddressListener) : BaseObservableViewModel() {
    var shippingAddress: UserShipping = UserShipping()
        @Bindable
        get() = field

    fun onClickCancel() = mListener.closeActivity(Activity.RESULT_CANCELED, false)
    fun onClickSubmit() = checkEmptyField { updateShippingMessage() }

    private fun checkEmptyField(task: () -> Unit) {
        when {
            shippingAddress.shippingName.isEmpty() -> showMessage("배송지 명을 입력해주세요")
            shippingAddress.zip.isEmpty() -> showMessage("우편번호를 입력해주세요")
            shippingAddress.address.isEmpty() -> showMessage("주소를 입력해주세요")
            shippingAddress.detailAddress.isEmpty() -> showMessage("상세주소를 입력해주세요")
            shippingAddress.recipientName.isEmpty() -> showMessage("수령인을 입력해주세요")
            shippingAddress.recipientMobile.isEmpty() -> showMessage("연락처를 입력해주세요")
            else -> task()
        }
    }

    private fun updateShippingMessage() {
        val userId = JWT(Preferences.getToken().accessToken).getClaim("userId").asInt()
        if (userId != null) {
            UserServer.updateUserShippingAddress(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { mListener.closeActivity(Activity.RESULT_OK, true) })
            }, userId, shippingAddress.id, shippingAddress)
        } else {
            ToastUtil.showMessage("로그인이 필요한 서비스입니다.")
            mListener.closeActivity(Activity.RESULT_CANCELED, false)
        }
    }

    private fun showMessage(message: String) = ToastUtil.showMessage(message)
}