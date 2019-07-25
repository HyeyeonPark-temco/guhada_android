package io.temco.guhada.data.viewmodel.mypage.repository

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.RecentDealEntity
import io.temco.guhada.data.model.product.Product
import java.util.*
import kotlin.collections.ArrayList

class MyPageRecentRepository(val context : Context, val disposable : CompositeDisposable ){
    private val db : GuhadaDB by lazy { GuhadaDB.getInstance(this.context as Activity)!! }

    // 메인 홈 list data
    private var list = SingleLiveEvent<ArrayList<Product>>()

    fun getList() : SingleLiveEvent<ArrayList<Product>> {
        if (list.value.isNullOrEmpty()){
            list.value = ArrayList()
            setInitData()
        }
        return list
    }

    fun setInitData(){
        disposable.add(Observable.fromCallable<List<Product>> {
            var allList = db.recentDealDao().getAll()
            var list = arrayListOf<Product>()
            if(!allList.isNullOrEmpty()){
                for(value in allList){
                    var product = Gson().fromJson(value.data,Product::class.java)
                    product.dealId = value.dealId
                    list.add(product)
                }
            }
            list
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe { result ->
            list.value!!.addAll(result)
            list.value = list.value
        })
    }


    fun destroy(){
        try{
            GuhadaDB.destroyInstance()
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
    }
}