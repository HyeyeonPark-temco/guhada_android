package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.LoginServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ShippingAddressViewModel : BaseObservableViewModel() {
    var userId: Int = -1
    var shippingAddresses: MutableLiveData<MutableList<UserShipping>> = MutableLiveData()
        @Bindable
        get() = field

    fun getUserShippingAddress() {
        LoginServer.getUserShippingAddress(OnServerListener { success, o ->
            if (success) {
                val model = o as BaseModel<*>
                when (model.resultCode) {
                    Flag.ResultCode.SUCCESS -> {
                        this.shippingAddresses.postValue(model.data as MutableList<UserShipping>)
                        notifyPropertyChanged(BR.shippingAddresses)
                    }
                    Flag.ResultCode.DATA_NOT_FOUND -> {
                        // EMPTY
                    }
                }
            } else {
                CommonUtil.debug(o as String)
            }
        }, userId)
    }

}