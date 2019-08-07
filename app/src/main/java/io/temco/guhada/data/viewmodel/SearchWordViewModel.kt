package io.temco.guhada.data.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.SearchWordEntity
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.search.*
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.ProductFilterListActivity
import io.temco.guhada.view.activity.SearchWordActivity
import io.temco.guhada.view.adapter.SearchWordAdapter
import java.util.*
import kotlin.collections.ArrayList
/**
 * @author park jungho
 * 검색화면에서 사용하는 viewmodel
 */
class SearchWordViewModel (val context : Context, private val disposable : CompositeDisposable, val db : GuhadaDB)  : BaseObservableViewModel() {
    private var repository: SearchWordRepository = SearchWordRepository(context, disposable, db)
    private val _listRecentData : SingleLiveEvent<ArrayList<SearchWord>> = repository.getRecentList()
    private val _listPopularData : SingleLiveEvent<ArrayList<SearchWord>> = repository.getPopularList()
    private val _listAutoCompleteData : SingleLiveEvent<ArrayList<SearchWord>> = repository.setInitAutoCompleteList()

    val listRecentData : LiveData<ArrayList<SearchWord>> get() = _listRecentData
    val listPopularData : LiveData<ArrayList<SearchWord>> get() = _listPopularData
    val listAutoCompleteData : LiveData<ArrayList<SearchWord>> get() = _listAutoCompleteData

    private val adapterRecent = SearchWordAdapter(this,listRecentData.value!!)
    private val adapterPopular = SearchWordAdapter(this,listPopularData.value!!)
    private val adapterAutoComplete = SearchWordAdapter(this,listAutoCompleteData.value!!)

    fun getRecentAdapter() = adapterRecent
    fun getPopularAdapter() = adapterPopular
    fun getAutoCompleteAdapter() = adapterAutoComplete

    var isNewActivity : Boolean = false

    var tabVisibleSwitch = ObservableInt(0)
        @Bindable
        get() = field

    var recentEmptyViewVisible = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.recentEmptyViewVisible)
        }


    var autoCompleteViewVisible = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.recentEmptyViewVisible)
        }


    var emptyEditTextWord = ObservableBoolean(true) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyEditTextWord)
        }


    var autoCompleteFlag = ObservableBoolean(true)
        @Bindable
        get() = field


    fun clickTab(tabIndex : Int){
        tabVisibleSwitch.set(tabIndex)
        notifyPropertyChanged(BR.tabVisibleSwitch)
    }


    fun clickAutoComplete(){
        autoCompleteFlag.set(!autoCompleteFlag.get())
        notifyPropertyChanged(BR.autoCompleteFlag)
    }


    fun deleteAllRecentWord(){
        disposable.add(Observable.fromCallable<Boolean> {
            var isFlag = true
            try {
                adapterRecent.items.clear()
                db.searchWordDao().deleteAll()
            }catch (e : java.lang.Exception){
                isFlag = false
            }
            isFlag
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    adapterRecent.notifyDataSetChanged()
                    recentEmptyViewVisible.set(true)
                    notifyPropertyChanged(BR.recentEmptyViewVisible)
                    if (CustomLog.flag) CustomLog.L("SearchWordRepository", "setInitRecentData ", "init -----",result)
                }
        )
    }


     fun searchWordList(text : String){
         disposable.add(Observable.just(text).subscribeOn(Schedulers.io()).subscribe {
             db.searchWordDao().delete(text)
             var searchWordEntity = SearchWordEntity()
             searchWordEntity.initData(Calendar.getInstance().timeInMillis, text, "", "", "", "")
             if (CustomLog.flag) CustomLog.L("searchWordList", searchWordEntity.toString())
             db.searchWordDao().insert(searchWordEntity)
             var list = db.searchWordDao().getAll(21)
             if (CustomLog.flag) CustomLog.L("searchWordList list", list.toString())
             if (list.size >= 21) {
                 db.searchWordDao().delete(list[list.size - 1])
             }
         })
         if(isNewActivity){
             var intent = Intent(context as SearchWordActivity, ProductFilterListActivity::class.java)
             intent.putExtra("type", Type.ProductListViewType.SEARCH)
             intent.putExtra("search_word", text)
             context.startActivityForResult(intent, Flag.RequestCode.BASE)
             context.setResult(Activity.RESULT_FIRST_USER)
             context.overridePendingTransition(0,0)
             context.finish()
         }else{
             var intent = Intent(context as SearchWordActivity, ProductFilterListActivity::class.java)
             intent.putExtra("type", Type.ProductListViewType.SEARCH)
             intent.putExtra("search_word", text)
             context.startActivityForResult(intent, Flag.RequestCode.BASE)
             context.setResult(Activity.RESULT_OK)
             context.overridePendingTransition(0,0)
             context.finish()
         }
    }

    fun deleteRecentWord(text : String){
        disposable.add(Observable.just(text).subscribeOn(Schedulers.io()).subscribe {
            db.searchWordDao().delete(text)
        })
    }


    fun getAutoCompolete(txt : String){
        if(autoCompleteFlag.get()){
            if(txt.isNullOrEmpty()){
                autoCompleteViewVisible.set(false)
                notifyPropertyChanged(BR.autoCompleteViewVisible)
                getAutoCompleteAdapter().items.clear()
            }else{
                getAutoCompleteAdapter().items.clear()
                repository.getAutoCompleteList(txt, object : OnCallBackListener{
                    override fun callBackListener(resultFlag: Boolean, value: Any) {
                        autoCompleteViewVisible.set(true)
                        notifyPropertyChanged(BR.autoCompleteViewVisible)
                        getAutoCompleteAdapter().notifyDataSetChanged()
                    }
                })
            }
        }
    }

}



class SearchWordRepository(val context : Context, var disposable : CompositeDisposable, val db : GuhadaDB){

    // 메인 홈 list data
    private var recentList = SingleLiveEvent<ArrayList<SearchWord>>()
    private var popularList = SingleLiveEvent<ArrayList<SearchWord>>()
    private var autocompleteList = SingleLiveEvent<ArrayList<SearchWord>>()

    fun getRecentList() : SingleLiveEvent<ArrayList<SearchWord>> {
        if (recentList.value.isNullOrEmpty()){
            recentList.value = ArrayList()
            setInitRecentData()
        }
        return recentList
    }

    private fun setInitRecentData(){
        disposable.add(Observable.fromCallable<List<SearchWord>> {
            var allList = db.searchWordDao().getAll(20)
            var list = arrayListOf<SearchWord>()
            if(!allList.isNullOrEmpty()){
                for(value in allList){
                    var word = SearchRecent(value)
                    list.add(word)
                }
            }
            list
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    recentList.value!!.addAll(result)
                    recentList.value = recentList.value
                    if (CustomLog.flag) CustomLog.L("SearchWordRepository", "setInitRecentData ", "init -----",result)
                })
    }


    fun getPopularList() : SingleLiveEvent<ArrayList<SearchWord>> {
        if (popularList.value.isNullOrEmpty()){
            popularList.value = ArrayList()
            setInitPopularData()
        }
        return popularList
    }

    private fun setInitPopularData(){
        SearchServer.getSearchPopularKeyword(10,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        if (CustomLog.flag) CustomLog.L("SearchWordRepository", "setInitPopularData ", "init ----- o",o)
                        var data = (o as BaseModel<*>).data as Popular
                        var dataList : MutableList<SearchPopular> = arrayListOf()
                        for (value in data.keywords){
                            dataList.add(SearchPopular(value))
                        }
                        popularList.value!!.addAll(dataList)
                        popularList.value = popularList.value
                        if (CustomLog.flag) CustomLog.L("SearchWordRepository", "setInitPopularData ", "init -----",dataList)
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }


    fun setInitAutoCompleteList() : SingleLiveEvent<ArrayList<SearchWord>> {
        if (autocompleteList.value.isNullOrEmpty()){
            autocompleteList.value = ArrayList()
        }
        return autocompleteList
    }

    fun getAutoCompleteList(txt : String, listener : OnCallBackListener){
        SearchServer.getSearchAutoComplete(txt,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        if (CustomLog.flag) CustomLog.L("SearchWordRepository", "getAutoCompleteList ", "init ----- o",o)
                        var data = (o as BaseModel<*>).data as AutoComplete
                        var dataList : MutableList<SearchAutoComplete> = arrayListOf()
                        for (value in data.name){
                            dataList.add(SearchAutoComplete(value,txt))
                        }
                        autocompleteList.value!!.addAll(dataList)
                        autocompleteList.value = autocompleteList.value
                        listener.callBackListener(true, 0)
                        if (CustomLog.flag) CustomLog.L("SearchWordRepository", "getAutoCompleteList ", "init -----",dataList)
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }


}