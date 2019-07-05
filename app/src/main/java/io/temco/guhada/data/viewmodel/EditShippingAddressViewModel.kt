package io.temco.guhada.data.viewmodel

import android.app.Activity
import io.temco.guhada.common.listener.OnEditShippingAddressListener
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class EditShippingAddressViewModel(val mListener: OnEditShippingAddressListener) : BaseObservableViewModel() {
    var shippingAddress: UserShipping = UserShipping()

    fun onClickCancel() = mListener.closeActivity(Activity.RESULT_CANCELED, false)
    fun onClickSubmit() = mListener.closeActivity(Activity.RESULT_OK, true)
}