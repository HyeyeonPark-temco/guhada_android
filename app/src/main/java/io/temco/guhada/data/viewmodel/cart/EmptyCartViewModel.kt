package io.temco.guhada.data.viewmodel.cart

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class EmptyCartViewModel() : BaseObservableViewModel() {
    private val UNIT_PER_PAGE = 3
    var mDealList = MutableLiveData<MutableList<Deal>>(mutableListOf())

    fun getDeals() {
        ProductServer.getProductListByOnlyPage(OnServerListener  { success, o ->
            if (success) {
                if(o is BaseModel<*>)
                    mDealList.postValue((o as BaseModel<MutableList<Deal>>).data)
            }
        }, unitPerPage = UNIT_PER_PAGE)
    }

    fun onClickContinue(){
        ToastUtil.showMessage("메인으로 이동")
    }
}