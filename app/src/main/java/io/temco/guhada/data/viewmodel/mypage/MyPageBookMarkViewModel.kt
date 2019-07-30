package io.temco.guhada.data.viewmodel.mypage

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.data.viewmodel.mypage.repository.MyPageProductBookMarkRepository
import io.temco.guhada.view.adapter.mypage.MyPageProductListAdapter

/**
 * 19.07.22
 * @author park jungho
 *
 * 찜한상품
    - 제품상세에 북마크 한 제품의 리스트가 보여짐
    - 각 제품별로 삭제(x) 누르면 별도의 팝업 없이 바로 삭제
    - 웹 프론트 작업 된 내용 공유
        ○ 내 찜한상품 // /my-page-controller/getUserLikeProductsUsingGET
        ○ 내 찜한상품 // getUserLikeProductsUsingGET
 *
 */
class MyPageBookMarkViewModel (val context : Context, var mDisposable : CompositeDisposable) : BaseObservableViewModel() {
    val db : GuhadaDB = GuhadaDB.getInstance(this.context as Activity)!!
    var repository: MyPageProductBookMarkRepository = MyPageProductBookMarkRepository(context,mDisposable,db)

    private val _listData : SingleLiveEvent<ArrayList<Product>> = repository.getList()
    val listData : LiveData<ArrayList<Product>> get() = _listData
    private val _itemSize : SingleLiveEvent<Int> = repository.getItemSize()
    val totalItemSize : SingleLiveEvent<Int> get() = _itemSize

    private val adapter = MyPageProductListAdapter(this,listData.value!!)

    fun getListAdapter() = adapter

    fun reloadRecyclerView(listener : OnSwipeRefreshResultListener){
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "reloadRecyclerView ", "init -----")
        adapter.items?.run{ clear() }
        repository.setInitData(listener)
    }

    override fun destroyModel() {
        super.destroyModel()
        try{
            mDisposable.dispose()
            GuhadaDB.destroyInstance()
        }catch (e : Exception){
            if(CustomLog.flag) CustomLog.E(e)
        }
    }

    fun onClickDeleteAll() {
        mDisposable.add(Observable.fromCallable<Boolean> {
            var isFlag = true
            try {
                adapter.items.clear()
                db.recentDealDao().deleteAll()
            }catch (e : java.lang.Exception){
                isFlag = false
            }
            isFlag
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    if(result){
                        totalItemSize.value = adapter.itemCount
                        adapter.notifyDataSetChanged()
                    }
                }
        )
    }



}