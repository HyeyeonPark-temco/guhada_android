package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.main.MainBaseModel
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.data.viewmodel.mypage.repository.MyPageRecentRepository
import io.temco.guhada.view.adapter.mypage.MyPageProductListAdapter

/**
 * 19.07.22
 * @author park jungho
 *
 * 최근본상품
    - 내부 디비를 이용하여 저장
    - 웹은 20개까지 저장
 *
 */
class MyPageRecentViewModel (val context : Context) : BaseObservableViewModel() {
    var mDisposable : CompositeDisposable = CompositeDisposable()
    private var repository: MyPageRecentRepository = MyPageRecentRepository(context,mDisposable)

    private val _listData : SingleLiveEvent<ArrayList<Product>> = repository.getList()
    val listData : LiveData<ArrayList<Product>> get() = _listData

    private val adapter = MyPageProductListAdapter(this,listData.value!!)

    fun getListAdapter() = adapter

    override fun destroyModel() {
        super.destroyModel()
        mDisposable.dispose()
        repository.destroy()
    }
}