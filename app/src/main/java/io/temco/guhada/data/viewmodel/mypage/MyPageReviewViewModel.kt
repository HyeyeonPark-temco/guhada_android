package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.review.*
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.mypage.MyPageReviewAdapter

/**
 * 19.07.22
 * @author park jungho
 *
 * 상품리뷰
    - 작성할 리뷰, 작성한 리뷰
    - 웹 프론트 작업 된 내용 공유
        ○ 상품 리뷰 userAPI 참고해서 작업하면됨
 *
 */
class MyPageReviewViewModel (val context : Context, val mLoadingIndicatorUtil: LoadingIndicatorUtil) : BaseObservableViewModel() {
    val mRequestManager: RequestManager by lazy { Glide.with(context) }

    var isLoading1 = true
    var isLoading2 = true

    val INIT_PAGE_NUMBER = 0
    private var repository = MyPageReviewRepository(this@MyPageReviewViewModel)
    private val _listAvailableReviewOrder : SingleLiveEvent<ArrayList<MyPageReviewBase>> = repository.getAvailableReviewOrderList()
    private val _listUserMyPageReview : SingleLiveEvent<ArrayList<MyPageReviewBase>> = repository.getUserMyPageReviewList()

    val listAvailableReviewOrder : LiveData<ArrayList<MyPageReviewBase>> get() = _listAvailableReviewOrder
    val listUserMyPageReview : LiveData<ArrayList<MyPageReviewBase>> get() = _listUserMyPageReview

    private val adapterAvailable= MyPageReviewAdapter(this,listAvailableReviewOrder.value!!)
    private val adapterReview = MyPageReviewAdapter(this,listUserMyPageReview.value!!)

    fun getAvailableAdapter() = adapterAvailable
    fun getReviewAdapter() = adapterReview

    var pageNum1 = INIT_PAGE_NUMBER
    var totalPageNum1 = -1

    var pageNum2 = INIT_PAGE_NUMBER
    var totalPageNum2 = -1

    var mypageReviewTabVisibleSwitch = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageReviewTabVisibleSwitch)
        }

    var tab1EmptyViewVisible = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.tab1EmptyViewVisible)
        }

    var tab2EmptyViewVisible = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.tab2EmptyViewVisible)
        }

    var mypageReviewtab1Title = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageReviewtab1Title)
        }

    var mypageReviewtab2Title = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageReviewtab2Title)
        }


    fun reloadRecyclerView(listener : OnSwipeRefreshResultListener){
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "reloadRecyclerView ", "init -----")
        if(mypageReviewTabVisibleSwitch.get() == 0){
            pageNum1 = INIT_PAGE_NUMBER
            adapterAvailable.items?.run{ clear() }
            repository.setInitAvailableReviewOrderList(pageNum1,listener)
        }else{
            pageNum2 = INIT_PAGE_NUMBER
            adapterReview.items?.run{ clear() }
            repository.setInitUserMyPageReviewList(pageNum2,listener)
        }
    }

    fun reloadRecyclerViewAll(){
        pageNum1 = INIT_PAGE_NUMBER
        adapterAvailable.items?.run{ clear() }
        repository.setInitAvailableReviewOrderList(pageNum1,null)

        pageNum2 = INIT_PAGE_NUMBER
        adapterReview.items?.run{ clear() }
        repository.setInitUserMyPageReviewList(pageNum2,null)
    }


    fun reloadRecyclerMyReviewList(){
        pageNum2 = INIT_PAGE_NUMBER
        adapterReview.items?.run{ clear() }
        repository.setInitUserMyPageReviewList(pageNum2,null)
    }

    fun clickTab(tabIndex : Int){
        mypageReviewTabVisibleSwitch.set(tabIndex)
    }

    fun getMoreTab1List() {
        pageNum1 += 1
        repository.getAvailableReviewOrderMore(pageNum1,null)
    }

    fun getMoreTab2List() {
        pageNum2 += 1
        repository.getUserMyPageReviewMore(pageNum2,null)
    }

    fun deleteMyReview(productId : Long, reviewId : Long, listener : OnCallBackListener){
        repository.deleteReview(productId, reviewId, listener)
    }


}



class MyPageReviewRepository(val model : MyPageReviewViewModel){
    private var availableReviewOrderList = SingleLiveEvent<ArrayList<MyPageReviewBase>>()
    private var userMyPageReviewList = SingleLiveEvent<ArrayList<MyPageReviewBase>>()

    fun getAvailableReviewOrderList() : SingleLiveEvent<ArrayList<MyPageReviewBase>> {
        if (availableReviewOrderList.value.isNullOrEmpty()){
            availableReviewOrderList.value = ArrayList()
            setInitAvailableReviewOrderList(model.INIT_PAGE_NUMBER,null)
        }
        return availableReviewOrderList
    }

    fun setInitAvailableReviewOrderList(page : Int, listener: OnSwipeRefreshResultListener?){
        getAvailableReviewOrderMore(page,listener)
    }


    fun getAvailableReviewOrderMore(page : Int, listener: OnSwipeRefreshResultListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null){
                        OrderServer.getMypageReviewAvailableList(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var startRange = model.getAvailableAdapter().items.size
                                        var data = (o as BaseModel<*>).data as MyPageOrderReview
                                        if(!data.orderItemList.isNullOrEmpty()){
                                            if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getAvailableReviewOrderMore ", o)
                                            if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getAvailableReviewOrderMore ", "init ----- list",data)
                                            model.mypageReviewtab1Title.set(data.count)
                                            availableReviewOrderList.value!!.addAll(data.orderItemList)
                                            if(CustomLog.flag)CustomLog.L("MyPageReviewRepository 2 getMoreClaimList1","data.totalPage ",data.totalPage ,"data.page",data.page)
                                            if(data.totalPage > 0 && (data.totalPage >= data.page)){
                                                model.totalPageNum1 = data.totalPage
                                                model.pageNum1 = data.page
                                            }
                                            if(CustomLog.flag)CustomLog.L("MyPageReviewRepository 2 getMoreClaimList1","mViewModel.totalPageNum1",model.totalPageNum1,"mViewModel.pageNum1",model.pageNum1)
                                            model.tab1EmptyViewVisible.set(false)
                                            if(startRange == 0){
                                                model.getAvailableAdapter().notifyDataSetChanged()
                                            }else{
                                                model.getAvailableAdapter().notifyItemRangeChanged(startRange, model.getAvailableAdapter().items.size)
                                            }
                                            listener?.onResultCallback()
                                            model.isLoading1 = false
                                        }
                                    },
                                    dataNotFoundTask = { if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getAvailableReviewOrderMore dataNotFoundTask ") },
                                    failedTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getAvailableReviewOrderMore failedTask ",o.toString())
                                        listener?.onResultCallback()
                                    },
                                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getAvailableReviewOrderMore userLikeNotFoundTask ") },
                                    serverRuntimeErrorTask = {  if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getAvailableReviewOrderMore serverRuntimeErrorTask ") },
                                    dataIsNull = {listener?.onResultCallback()}
                            )
                        }, accessToken = it, page = (page+1))
                    }
                }, invalidTokenTask = {
                    if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getAvailableReviewOrderMore invalidTokenTask ")
                    listener?.onResultCallback()
        })
    }


    fun getUserMyPageReviewList() : SingleLiveEvent<ArrayList<MyPageReviewBase>> {
        if (userMyPageReviewList.value.isNullOrEmpty()){
            userMyPageReviewList.value = ArrayList()
            setInitUserMyPageReviewList(model.INIT_PAGE_NUMBER,null)
        }
        return userMyPageReviewList
    }

    fun setInitUserMyPageReviewList(page : Int, listener: OnSwipeRefreshResultListener?){
        getUserMyPageReviewMore(page, listener)
    }

    fun getUserMyPageReviewMore(page : Int, listener: OnSwipeRefreshResultListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null){
                        UserServer.getMypageReviewList(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var startRange = model.getReviewAdapter().items.size
                                        var data = (o as BaseModel<*>).data as MyPageReview
                                        model.mypageReviewtab2Title.set(data.totalElements)
                                        userMyPageReviewList.value!!.addAll(data.content)
                                        if(CustomLog.flag)CustomLog.L("MyPageReviewRepository 2 getMoreClaimList2","data.totalPages",data.totalPages,"data.pageable.pageNumber",data.pageable.pageNumber)
                                        if(data.totalPages > 0 && (data.totalPages > data.pageable.pageNumber)){
                                            model.totalPageNum2 = data.totalPages
                                            model.pageNum2 = data.pageable.pageNumber
                                        }
                                        if(CustomLog.flag)CustomLog.L("MyPageReviewRepository 2 getMoreClaimList2","mViewModel.totalPageNum2",model.totalPageNum2,"mViewModel.pageNum2",model.pageNum2)
                                        model.tab2EmptyViewVisible.set(false)

                                        if(startRange == 0){
                                            model.getReviewAdapter().notifyDataSetChanged()
                                        }else{
                                            model.getReviewAdapter().notifyItemRangeChanged(startRange, model.getReviewAdapter().items.size)
                                        }
                                        listener?.onResultCallback()
                                        model.isLoading2 = false
                                    },
                                    dataNotFoundTask = { if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getUserMyPageReviewMore dataNotFoundTask ") },
                                    failedTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getMypageReviewList failedTask ",o.toString())
                                        listener?.onResultCallback()
                                    },
                                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getUserMyPageReviewMore userLikeNotFoundTask ") },
                                    serverRuntimeErrorTask = {  if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getUserMyPageReviewMore serverRuntimeErrorTask ") },
                                    dataIsNull = {listener?.onResultCallback()}
                            )
                        }, accessToken = it, page = page, size = 20)
                    }
                }, invalidTokenTask = {
                    if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "getUserMyPageReviewMore invalidTokenTask ")
                    listener?.onResultCallback()
        })
    }



    fun deleteReview(productId : Long, reviewId : Long, listener : OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null){
                        UserServer.deleteReviewData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var data = (o as BaseModel<*>).data as Any
                                        if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "deleteReview failedTask ",data.toString())
                                        listener.callBackListener(true,data)
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false,"dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false,"failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false,"userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = {  listener.callBackListener(false,"serverRuntimeErrorTask") },
                                    dataIsNull = { listener.callBackListener(false,"dataIsNull") }
                            )
                        }, accessToken = it, productId = productId, reviewId = reviewId)
                    }
                }, invalidTokenTask = {  listener.callBackListener(false,"invalidTokenTask") })
    }

}