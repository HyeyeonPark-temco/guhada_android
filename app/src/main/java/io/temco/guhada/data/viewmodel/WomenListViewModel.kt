package io.temco.guhada.data.viewmodel


import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * @author park jungho
 * 19.07.18
 * 메인 여성 리스트 CustomView ViewModel
 */
class WomenListViewModel : BaseObservableViewModel() {
   /* private var repository: HomeListRepository = HomeListRepository()

    private val _clickView = SingleLiveEvent<Int>()
    private val _listData : MutableLiveData<ArrayList<MainBaseModel>> = repository.getList()
    private val adapter = HomeListAdapter(this,_listData.value!!)

    val listData :LiveData<ArrayList<MainBaseModel>> get() = _listData

    fun getAdapter() = adapter

    fun setAddDataAll(items: ArrayList<MainBaseModel>) {
        this.adapter.addAll(items)
        this.adapter.notifyDataSetChanged()
        if (CustomLog.flag) CustomLog.L("WomenListViewModel LIFECYCLE", "setAddDataAll----------------itemCount ",adapter.itemCount)
    }*/


}