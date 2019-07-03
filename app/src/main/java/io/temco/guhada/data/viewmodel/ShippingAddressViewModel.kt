package io.temco.guhada.data.viewmodel

import android.app.Activity
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.LoginServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ShippingAddressViewModel(val mListener: OnShippingAddressListener) : BaseObservableViewModel() {
    var userId: Int = -1
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
            if (success) {
                val model = o as BaseModel<*>
                when (model.resultCode) {
                    Flag.ResultCode.SUCCESS -> {
                        this.shippingAddresses.postValue(model.data as MutableList<UserShipping>)
                        emptyVisibility = ObservableInt(View.GONE)

                        notifyPropertyChanged(BR.shippingAddresses)
                        notifyPropertyChanged(BR.emptyVisibility)
                    }
                    Flag.ResultCode.DATA_NOT_FOUND -> {
                        emptyVisibility = ObservableInt(View.VISIBLE)
                        notifyPropertyChanged(BR.emptyVisibility)
                    }
                }
            } else {
                emptyVisibility = ObservableInt(View.VISIBLE)
                notifyPropertyChanged(BR.emptyVisibility)
                CommonUtil.debug(o as String)
            }
        }, userId)
    }

    fun onClickSubmit() {
        mListener.closeActivity(Activity.RESULT_OK, true)
    }

    fun onClickCancel() {
        mListener.closeActivity(Activity.RESULT_CANCELED, false)
    }
}