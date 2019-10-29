package io.temco.guhada.data.viewmodel.cart

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.main.HomeType
import io.temco.guhada.data.model.main.SubTitleItemList
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class EmptyCartViewModel() : BaseObservableViewModel() {
    private val UNIT_PER_PAGE = 3
    var mDealList = MutableLiveData<MutableList<Deal>>(mutableListOf())

    fun getDeals() {
        SearchServer.getProductByBestItem(UNIT_PER_PAGE,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var list : MutableList<Deal> = mutableListOf()
                        for (d in newArrival.allList!!.iterator()){
                            list.add(d)
                        }
                        mDealList.postValue(list)
                    },
                    dataNotFoundTask = {   },
                    failedTask = {  }
            )
        })
    }

    fun onClickContinue(){
        ToastUtil.showMessage("메인으로 이동")
    }
}