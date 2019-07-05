package io.temco.guhada.data.viewmodel

import android.app.Activity
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.executeByResultCode
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.server.LoginServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ShippingAddressViewModel(val mListener: OnShippingAddressListener) : BaseObservableViewModel() {
    var userId: Int = -1
    var deletePos = -1
    var prevSelectedItem: UserShipping = UserShipping()
    var selectedItem: UserShipping = UserShipping()
    var shippingAddresses: MutableLiveData<MutableList<UserShipping>> = MutableLiveData()
        @Bindable
        get() = field
    var emptyVisibility = ObservableInt(View.VISIBLE)
        @Bindable
        get() = field

    /**
     * @exception IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 1 column 36 path $.data
     */
    fun getUserShippingAddress() {
        LoginServer.getUserShippingAddress(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = { model ->
                        this.shippingAddresses.postValue(model.data as MutableList<UserShipping>)
                        emptyVisibility = ObservableInt(View.GONE)
                        notifyPropertyChanged(BR.shippingAddresses)
                        notifyPropertyChanged(BR.emptyVisibility)
                    },
                    failedTask = {
                        emptyVisibility = ObservableInt(View.VISIBLE)
                        notifyPropertyChanged(BR.emptyVisibility)
                        CommonUtil.debug(o as String)
                    },
                    dataNotFoundTask = {
                        emptyVisibility = ObservableInt(View.VISIBLE)
                        notifyPropertyChanged(BR.emptyVisibility)
                    })
        }, userId)
    }

    fun deleteShippingAddress(shippingAddressId: Int) {
        LoginServer.deleteUserShippingAddress(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = {
                        mListener.notifyDeleteItem()
                    },
                    failedTask = {
                        ToastUtil.showMessage("배송지 삭제에 실패했습니다. 다시 시도해주세요.")
                    })
        }, userId, shippingAddressId)
    }

    fun editShippingAddress(position: Int) {
        val shippingAddress = shippingAddresses.value?.get(position)
        if(shippingAddress != null){
            mListener.redirectEditShippingAddressActivity(shippingAddress)
        }
    }

    fun onClickSubmit() {
        mListener.closeActivity(Activity.RESULT_OK, true)
    }

    fun onClickCancel() {
        mListener.closeActivity(Activity.RESULT_CANCELED, false)
    }
}