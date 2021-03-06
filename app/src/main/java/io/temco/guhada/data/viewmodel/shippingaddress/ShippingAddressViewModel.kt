package io.temco.guhada.data.viewmodel.shippingaddress

import android.app.Activity
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.executeByResultCode
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

open class ShippingAddressViewModel(val mListener: OnShippingAddressListener) : BaseObservableViewModel() {
    var userId: Int = -1
    var prevSelectedItem: UserShipping = UserShipping()
    var selectedItem: UserShipping = UserShipping()
    var newItem = UserShipping()
    var shippingAddresses: MutableLiveData<MutableList<UserShipping>> = MutableLiveData()
        @Bindable
        get() = field
    var emptyVisibility = ObservableInt(View.VISIBLE)
        @Bindable
        get() = field
    var viewpagerPos = 0
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.viewpagerPos)
        }

    fun getUserShippingAddress() {
        if (Preferences.getToken() != null) {
            val accessToken = Preferences.getToken().accessToken
            if (accessToken != null) {
                userId = JWT(accessToken).getClaim("userId").asInt() ?: -1
            }

            UserServer.getUserShippingAddress(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { model ->
                            this.shippingAddresses.postValue(model.data as MutableList<UserShipping>)
                            emptyVisibility = ObservableInt(View.GONE)
                            notifyPropertyChanged(BR.shippingAddresses)
                            notifyPropertyChanged(BR.emptyVisibility)
                        },
                        failedTask = {
                            emptyVisibility = ObservableInt(View.VISIBLE)
                            notifyPropertyChanged(BR.emptyVisibility)
                        },
                        dataNotFoundTask = {
                            emptyVisibility = ObservableInt(View.VISIBLE)
                            notifyPropertyChanged(BR.emptyVisibility)
                        })
            }, userId)
        }else {
            this.shippingAddresses.postValue(mutableListOf())
            emptyVisibility = ObservableInt(View.VISIBLE)
            notifyPropertyChanged(BR.emptyVisibility)
        }
    }

    fun deleteShippingAddress(shippingAddressId: Int) {
        val token = Preferences.getToken()
        if (token != null) {
            val accessToken = Preferences.getToken().accessToken
            if (accessToken != null) {
                userId = JWT(accessToken).getClaim("userId").asInt() ?: -1
            }

            UserServer.deleteUserShippingAddress(OnServerListener { success, o ->
                executeByResultCode(success, o,
                        successTask = {
                            mListener.notifyDeleteItem()
                        },
                        failedTask = {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_delete_failed))
                        })
            }, userId, shippingAddressId)
        }
    }

    fun editShippingAddress(position: Int) {
        val shippingAddress = shippingAddresses.value?.get(position)
        if (shippingAddress != null) {
            mListener.redirectEditShippingAddressActivity(shippingAddress)
        }
    }

    fun checkEmptyField(task: () -> Unit) {
        when {
            newItem.shippingName.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_empty_shippingname))
            newItem.zip.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_empty_zip))
            newItem.address.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_empty_address))
            newItem.detailAddress.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_empty_detailaddress))
            newItem.recipientName.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_empty_recipientname))
            newItem.recipientMobile.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_message_empty_recipientmobile))
            else -> task()
        }
    }

    fun onClickCancel() = mListener.closeActivity(Activity.RESULT_CANCELED, null)

    fun onClickUpdate() = mListener.closeActivity(Activity.RESULT_OK, selectedItem)

    fun onClickAdd() = checkEmptyField { mListener.closeActivity(Activity.RESULT_OK, newItem) }

    fun redirectSearchZipActivity() = mListener.redirectSearchZipActivity()
}