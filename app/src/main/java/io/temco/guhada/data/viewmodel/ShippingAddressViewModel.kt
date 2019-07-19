package io.temco.guhada.data.viewmodel

import android.app.Activity
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil.Companion.executeByResultCode
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ShippingAddressViewModel(val mListener: OnShippingAddressListener) : BaseObservableViewModel() {
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

    /**
     * @exception IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 1 column 36 path $.data
     *
     * TODO 결과 값이 서로 달라 확인 필요 - 배송지 목록 관련 확인
     */
    fun getUserShippingAddress() {
        UserServer.getUserShippingAddress(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = { model ->
                        this.shippingAddresses.postValue(model.list as MutableList<UserShipping>)
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
        UserServer.deleteUserShippingAddress(OnServerListener { success, o ->
            executeByResultCode(success, o,
                    successTask = {
                        mListener.notifyDeleteItem()
                    },
                    failedTask = {
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_delete_failed))
                    })
        }, userId, shippingAddressId)
    }

    fun editShippingAddress(position: Int) {
        val shippingAddress = shippingAddresses.value?.get(position)
        if (shippingAddress != null) {
            mListener.redirectEditShippingAddressActivity(shippingAddress)
        }
    }

    private fun checkEmptyField(task: () -> Unit) {
        when {
            newItem.shippingName.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_shippingname))
            newItem.zip.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_zip))
            newItem.address.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_address))
            newItem.detailAddress.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_detailaddress))
            newItem.recipientName.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_recipientname))
            newItem.recipientMobile.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingaddress_messaeg_empty_recipientmobile))
            else -> task()
        }
    }

    fun onClickCancel() = mListener.closeActivity(Activity.RESULT_CANCELED, null)

    fun onClickUpdate() = mListener.closeActivity(Activity.RESULT_OK, selectedItem)

    fun onClickAdd() = checkEmptyField { mListener.closeActivity(Activity.RESULT_OK, newItem) }

    fun redirectSearchZipActivity() = mListener.redirectSearchZipActivity()
}