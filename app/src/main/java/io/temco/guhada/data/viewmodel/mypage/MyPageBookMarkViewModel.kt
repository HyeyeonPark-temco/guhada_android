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
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.BookMarkProduct
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
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
    var isLoading = true

    val listData : LiveData<ArrayList<Deal>> get() = _listData

    var currentPage : Int = 1
    var totalPage : Int = -1

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
                if(getListAdapter().items.isEmpty()) emptyViewVisible.set(true)
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


class MyPageProductBookMarkRepository (val model : MyPageBookMarkViewModel) {

    // 메인 홈 list data
    private var list = SingleLiveEvent<ArrayList<Deal>>()

    fun getList(): SingleLiveEvent<ArrayList<Deal>> {
        if (list.value.isNullOrEmpty()) {
            list.value = ArrayList()
            setInitData(null)
        }
        return list
    }


    fun setInitData(listener: OnSwipeRefreshResultListener?) {
        getMyBookMarkProductList(1,listener)
    }


    fun getMyBookMarkProductList(page : Int, listener: OnSwipeRefreshResultListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null){
                        GatewayServer.getBookMarkProduct(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var startRange = model.getListAdapter().items.size
                                        var data = (o as BaseModel<*>).data as BookMarkProduct
                                        if (CustomLog.flag) CustomLog.L("MyPageBookMarkLayout", "setInitData ", "init ----- list",data)
                                        model.totalElement.set(data.totalElements.toString())
                                        list.value!!.addAll(data.deals)
                                        model.totalPage = data.totalPage
                                        if (CustomLog.flag) CustomLog.L("MyPageBookMarkLayout", "setInitData ", "init ----- model.totalPage",model.totalPage,"currentPage",model.currentPage)
                                        list!!.value = list!!.value
                                        if(startRange == 0){
                                            model.getListAdapter().notifyDataSetChanged()
                                        }else{
                                            model.getListAdapter().notifyItemRangeChanged(startRange, model.getListAdapter().items.size)
                                        }
                                        model.isLoading = false
                                        listener?.onResultCallback()

                                    },
                                    dataNotFoundTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData dataNotFoundTask ")
                                        listener?.onResultCallback()
                                    },
                                    failedTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData failedTask ",o.toString())
                                        listener?.onResultCallback()
                                    },
                                    userLikeNotFoundTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData userLikeNotFoundTask ")
                                        listener?.onResultCallback()
                                    },
                                    serverRuntimeErrorTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData serverRuntimeErrorTask ")
                                        listener?.onResultCallback()
                                    },
                                    dataIsNull = {listener?.onResultCallback()}
                            )
                        }, accessToken = it, page = page)
                    }
                }, invalidTokenTask = {  if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData invalidTokenTask ") })
    }



    fun deleteBookMark(target : String, targetId: Long, callBackListener: OnCallBackListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    UserServer.deleteBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if(CustomLog.flag)CustomLog.L("deleteBookMark","successTask")
                                    callBackListener?.callBackListener(true,0)
                                },
                                dataNotFoundTask = { callBackListener?.callBackListener(false,"dataNotFoundTask") },
                                failedTask = { callBackListener?.callBackListener(false,"failedTask") },
                                userLikeNotFoundTask = { callBackListener?.callBackListener(false,"userLikeNotFoundTask") },
                                serverRuntimeErrorTask = { callBackListener?.callBackListener(false,"serverRuntimeErrorTask") }
                        )
                    }, accessToken = it, target = target,targetId = targetId)
                }, invalidTokenTask = { })
    }


    fun deleteBookMarkAll(target : String, callBackListener: OnCallBackListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    UserServer.deleteBookMarkAll(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if(CustomLog.flag)CustomLog.L("deleteBookMark","successTask",o.toString())
                                    callBackListener?.callBackListener(true,0)
                                },
                                dataNotFoundTask = { callBackListener?.callBackListener(false,"dataNotFoundTask") },
                                failedTask = {callBackListener?.callBackListener(false,"failedTask") },
                                userLikeNotFoundTask = {callBackListener?.callBackListener(false,"userLikeNotFoundTask") },
                                serverRuntimeErrorTask = { callBackListener?.callBackListener(false,"serverRuntimeErrorTask")}
                        )
                    }, accessToken = it, target = target)
                }, invalidTokenTask = { })
    }

}