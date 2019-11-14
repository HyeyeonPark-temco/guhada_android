package io.temco.guhada.data.viewmodel

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.CardInterest
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.SettleServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CardInterestViewModel : BaseObservableViewModel() {
    var mCardInterest = MutableLiveData(mutableListOf<CardInterest>())

    fun getCardInterest() {
        SettleServer.getCardInterst(OnServerListener { success, o ->
            if (success && (o as BaseModel<*>).resultCode == ResultCode.SUCCESS.flag) {
                if(o.data is MutableList<*>){
                    mCardInterest.postValue(o.data as MutableList<CardInterest>)
                }
            }
        })
    }
}