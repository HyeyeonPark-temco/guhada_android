package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.main.MainBaseModel
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.data.viewmodel.repository.HomeListRepository
import io.temco.guhada.view.adapter.main.HomeListAdapter
/**
 * @author park jungho
 * 19.07.18
 * 메인 홈 리스트 CustomView ViewModel
 */
class HomeListViewModel(val context : Context) : BaseObservableViewModel() {
    private var repository: HomeListRepository = HomeListRepository(context)

    private val _listData : SingleLiveEvent<ArrayList<MainBaseModel>> = repository.getList()
    private val adapter = HomeListAdapter(this,listData.value!!)

    val listData :LiveData<ArrayList<MainBaseModel>> get() = _listData

    fun getListAdapter() = adapter

    fun setAddDataAll(items: ArrayList<MainBaseModel>) {
        this.adapter.addAll(items)
        this.adapter.notifyDataSetChanged()
        if (CustomLog.flag) CustomLog.L("HomeListViewModel LIFECYCLE", items.toString())
        if (CustomLog.flag) CustomLog.L("HomeListViewModel LIFECYCLE", "setAddDataAll----------------itemCount ",adapter.itemCount)
    }



}