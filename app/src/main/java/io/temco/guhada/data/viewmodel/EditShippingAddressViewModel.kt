package io.temco.guhada.data.viewmodel

import android.app.Activity
import androidx.databinding.Bindable
import com.auth0.android.jwt.JWT
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
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
            shippingAddress.shippingName.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_shippingname))
            shippingAddress.zip.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_zip))
            shippingAddress.address.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_address))
            shippingAddress.detailAddress.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_detailaddress))
            shippingAddress.recipientName.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_recipientname))
            shippingAddress.recipientMobile.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_recipientmobile))
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
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            mListener.closeActivity(Activity.RESULT_CANCELED, false)
        }
    }
}