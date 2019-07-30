package io.temco.guhada.data.viewmodel.mypage.repository

import android.content.Context
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.product.Product

class MyPageProductBookMarkRepository (val context : Context, var disposable : CompositeDisposable, val db : GuhadaDB) {

    // 메인 홈 list data
    private var list = SingleLiveEvent<ArrayList<Product>>()
    private var itemSize = SingleLiveEvent<Int>()

    fun getList(): SingleLiveEvent<ArrayList<Product>> {
        if (list.value.isNullOrEmpty()) {
            list.value = ArrayList()
            setInitData(null)
        }
        return list
    }

    fun getItemSize(): SingleLiveEvent<Int> {
        if (!list.value.isNullOrEmpty()) {
            itemSize.value = list.value!!.size
        }
        return itemSize
    }


    fun setInitData(listener: OnSwipeRefreshResultListener?) {
        disposable.add(Observable.fromCallable<List<Product>> {
            var allList = db.recentDealDao().getAll(20)
            var list = arrayListOf<Product>()
            if (!allList.isNullOrEmpty()) {
                for (value in allList) {
                    var product = Gson().fromJson(value.data, Product::class.java)
                    product.dealId = value.dealId
                    list.add(product)
                }
            }
            var pdt = Product()
            pdt.sellerId = -99
            list.add(pdt)
            list
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    list.value!!.addAll(result)
                    list.value = list.value
                    itemSize.value = list.value!!.size
                    listener?.run { onResultCallback() }
                    if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "setInitData ", "init -----")
                })
    }
}