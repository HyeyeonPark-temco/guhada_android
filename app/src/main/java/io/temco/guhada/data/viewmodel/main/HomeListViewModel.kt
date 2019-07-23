package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.lifecycle.LiveData
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.main.MainBaseModel
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.data.viewmodel.main.repository.HomeListRepository
import io.temco.guhada.view.adapter.main.HomeListAdapter

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class HomeListViewModel(val context : Context) : BaseObservableViewModel() {
    private var repository: HomeListRepository = HomeListRepository(context)

    private val _listData : SingleLiveEvent<ArrayList<MainBaseModel>> = repository.getList()
    private val adapter = HomeListAdapter(this,listData.value!!)

    val listData :LiveData<ArrayList<MainBaseModel>> get() = _listData

    fun getListAdapter() = adapter

}