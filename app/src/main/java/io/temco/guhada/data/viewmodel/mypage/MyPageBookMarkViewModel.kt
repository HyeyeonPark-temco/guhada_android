package io.temco.guhada.data.viewmodel.mypage

import android.app.Activity
import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.BR
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.data.viewmodel.mypage.repository.MyPageProductBookMarkRepository
import io.temco.guhada.view.adapter.mypage.MyPageDealListAdapter

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
    val mRequestManager: RequestManager by lazy { Glide.with(context) }
    val db : GuhadaDB = GuhadaDB.getInstance(this.context as Activity)!!
    var repository: MyPageProductBookMarkRepository = MyPageProductBookMarkRepository(this)

    private val _listData : SingleLiveEvent<ArrayList<Deal>> = repository.getList()
    private val adapter = MyPageDealListAdapter(this,listData.value!!)

    val listData : LiveData<ArrayList<Deal>> get() = _listData

    var currentPage : Int = 1

    var totalElement = ObservableField("0") // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalElement)
        }

    var emptyViewVisible = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyViewVisible)
        }

    fun getListAdapter() = adapter

    fun getMyPageBookMarkList(){
        currentPage += 1
        repository.getMyBookMarkProductList(currentPage,null)
    }

    fun reloadRecyclerView(listener : OnSwipeRefreshResultListener){
        currentPage = 0
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


    fun onClickDelete(targetId : Long) {
        repository.deleteBookMark(Type.BookMarkTarget.PRODUCT.name, targetId, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag)CustomLog.L("MyPageBookMarkViewModel","onClickDelete",resultFlag,value)
                var v = totalElement.get()!!.toInt() -1
                totalElement.set(v.toString())
                getListAdapter().notifyDataSetChanged()
            }
        })
    }

    fun onClickDeleteAll() {
        if(getListAdapter().items.isNotEmpty()){
            repository.deleteBookMarkAll(Type.BookMarkTarget.PRODUCT.name, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    if(CustomLog.flag)CustomLog.L("MyPageBookMarkViewModel","onClickDeleteAll",resultFlag,value)
                    getListAdapter().items.clear()
                    totalElement.set("0")
                    getListAdapter().notifyDataSetChanged()
                    emptyViewVisible.set(true)
                    currentPage = 0
                }
            })
        }
    }





}