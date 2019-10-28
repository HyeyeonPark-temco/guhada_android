package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.model.claim.MyPageClaimSeller
import io.temco.guhada.data.model.claim.MyPageClaimSellerContent
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.mypage.MyPageClaimAdapter
import io.temco.guhada.view.adapter.mypage.MyPageSellerClaimAdapter

/**
 * 19.07.22
 * @author park jungho
 *
 * 상품문의
    - 웹 프론트 작업 된 내용 공유
        ○ /users/my-page/inquiries
 *
 */
class MyPageClaimViewModel (val context : Context) : BaseObservableViewModel() {
    val mRequestManager: RequestManager by lazy { Glide.with(context) }
    var repository = MyPageCliamRepository(this)
    val listData1 : MutableLiveData<ArrayList<MyPageClaim.Content>> = MutableLiveData()
    val listData2 : MutableLiveData<ArrayList<MyPageClaimSellerContent>> = MutableLiveData()

    private var adapter1 : MyPageClaimAdapter
    private var adapter2 : MyPageSellerClaimAdapter
    fun getListAdapter1() = adapter1
    fun getListAdapter2() = adapter2

    private var selectClaimStatusFilter = 0
    var selectedIndex = 0

    var pageNum1 = 0
    var totalPageNum1 = -1

    var pageNum2 = 0
    var totalPageNum2 = -1


    init {
        listData1.value = arrayListOf()
        listData2.value = arrayListOf()
        adapter1 = MyPageClaimAdapter(this,listData1.value!!)
        adapter2 = MyPageSellerClaimAdapter(this,listData2.value!!)
        repository.getList1()
        repository.getList2()
    }

    var mypageClaimTotalCountTxt = ObservableField<String>("0")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageClaimTotalCountTxt)
        }
    var mypageSellerClaimTotalCountTxt = ObservableField<String>("0")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageSellerClaimTotalCountTxt)
        }

    var mypageClaimTabVisibleSwitch = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageClaimTabVisibleSwitch)
        }

    var emptyClaimVisible1 = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyClaimVisible1)
        }


    var emptyClaimVisible2 = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyClaimVisible2)
        }

    fun clickTab(tabIndex : Int){
        mypageClaimTabVisibleSwitch.set(tabIndex)
    }


    fun reloadRecyclerView(listener : OnSwipeRefreshResultListener?){
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "reloadRecyclerView ", "init -----")
        if(mypageClaimTabVisibleSwitch.get() == 0){
            listData1.value?.clear()
            adapter1.items?.run{ clear() }
            adapter1.notifyDataSetChanged()
            repository.setInitData1(selectClaimStatusFilter,0,listener)
        }else if(mypageClaimTabVisibleSwitch.get() == 1){
            listData2.value?.clear()
            adapter2.items?.run{ clear() }
            adapter2.notifyDataSetChanged()
            repository.setInitData2(0,0,listener)
        }
    }

    var claimMessages: MutableList<String> = mutableListOf("전체","미답변","완료")
        @Bindable
        get() = field

    var selectedStatusMessage = ObservableField<String>("전체") // 스피너 표시 메세지
        @Bindable
        get() = field

    fun onShippingMemoSelected(position: Int) {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onShippingMemoSelected ", "position -----",position, "selectClaimStatusFilter",selectClaimStatusFilter)
        if (claimMessages.size > position && selectClaimStatusFilter != position) {
            selectClaimStatusFilter = position
            val message = claimMessages[position]
            selectedStatusMessage.set(message)
            notifyPropertyChanged(BR.selectedStatusMessage)
            reloadRecyclerView(null)
        }
    }

    fun deleteClaimItem(itemIndex : Int, productId : Long, inquiryId : Long, loading : LoadingIndicatorUtil){
        repository.deleteClaim(productId,inquiryId, OnServerListener { success, o ->
            loading.dismiss()
            if(success){
                var data = (o as BaseModel<JsonObject>)
                if(data.resultCode in 200..400){
                    var count = mypageClaimTotalCountTxt.get()!!.toInt()
                    mypageClaimTotalCountTxt.set((count-1).toString())
                    adapter1.items.removeAt(itemIndex)
                    adapter1.notifyDataSetChanged()
                }
            }
        })
    }



    fun deleteSellerClaimItem(itemIndex : Int, id : Int, loading : LoadingIndicatorUtil){
        var userId = CommonUtil.checkUserId()
        if(userId > 0){
            repository.deleteSellerClaim(userId = userId,id=id, listener =  OnServerListener { success, o ->
                loading.dismiss()
                if(success){
                    var data = (o as BaseModel<JsonObject>)
                    if(data.resultCode in 200..400){
                        var count = mypageSellerClaimTotalCountTxt.get()!!.toInt()
                        mypageSellerClaimTotalCountTxt.set((count-1).toString())
                        adapter2.items.removeAt(itemIndex)
                        adapter2.notifyDataSetChanged()
                    }
                }
            })
        }
    }

    fun getMoreCalimList(nextPage : Int) {
        repository.getMoreClaimList1(selectClaimStatusFilter,null, nextPage)
    }


    fun getMoreSellerCalimList(nextPage : Int) {
        repository.getMoreClaimList2(0,null, nextPage)
    }

}



class MyPageCliamRepository (val viewModel : MyPageClaimViewModel) {
    // 메인 홈 list data

    fun getList1() {
        viewModel.pageNum1 = 0
        viewModel.totalPageNum1 = -1
        setInitData1(0,viewModel.pageNum1,null)
    }

    fun getList2() {
        viewModel.pageNum2 = 0
        viewModel.totalPageNum2 = -1
        setInitData2(0,viewModel.pageNum1,null)
    }

    /**
     * 상품 문의
     */
    fun setInitData1(status : Int, pageNum : Int,listener : OnSwipeRefreshResultListener?) {
        getMoreClaimList1(status, listener, pageNum)
    }


    /**
     * 판매자 문의
     */
    fun setInitData2(status : Int, pageNum : Int, listener : OnSwipeRefreshResultListener?) {
        getMoreClaimList2(status, listener, pageNum)
    }

    fun getMoreClaimList1(status : Int,listener : OnSwipeRefreshResultListener?, nextPage : Int = 0){
        var statusValue = ""
        when(status){
            1->statusValue = "PENDING"
            2->statusValue = "COMPLETED"
        }
        ClaimServer.getMyPageClaimList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        if(nextPage > 1){
                            var idx = viewModel.listData1!!.value!!.size-1
                            if(viewModel.listData1!!.value!![idx].inquiry.id == 0L){
                                viewModel.listData1!!.value!!.removeAt(idx)
                            }
                        }
                        var data =  (o as BaseModel<*>).data as MyPageClaim
                        if(data.totalPages > 0 && (data.totalPages-1 > data.pageable.pageNumber)){
                            var page = MyPageClaim().Content()
                            viewModel.totalPageNum1 = data.totalPages
                            viewModel.pageNum1 = data.pageable.pageNumber
                            data.content.add(page)
                        }
                        if(data.content.size == 0 && data.pageable.pageNumber == 0){
                            viewModel.emptyClaimVisible1.set(true)
                        } else viewModel.emptyClaimVisible1.set(false)
                        viewModel.mypageClaimTotalCountTxt.set(data.totalElements.toString())

                        viewModel.getListAdapter1()!!.items.addAll(data.content)
                        viewModel.getListAdapter1().notifyDataSetChanged()
                        listener?.run { onResultCallback() }
                    },
                    dataNotFoundTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","dataNotFoundTask")
                        viewModel.emptyClaimVisible1.set(true)
                        viewModel.mypageClaimTotalCountTxt.set("0")
                        listener?.run { onResultCallback() }
                    },
                    failedTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","failedTask")
                        viewModel.emptyClaimVisible1.set(true)
                        viewModel.mypageClaimTotalCountTxt.set("0")
                        listener?.run { onResultCallback() }
                    },
                    serverRuntimeErrorTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverRuntimeErrorTask")
                        viewModel.emptyClaimVisible1.set(true)
                        viewModel.mypageClaimTotalCountTxt.set("0")
                        listener?.run { onResultCallback() }
                    },
                    serverLoginErrorTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverLoginErrorTask")
                        viewModel.emptyClaimVisible1.set(true)
                        viewModel.mypageClaimTotalCountTxt.set("0")
                        listener?.run { onResultCallback() }
                    },
                    dataIsNull = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","dataIsNull")
                        viewModel.emptyClaimVisible1.set(true)
                        viewModel.mypageClaimTotalCountTxt.set("0")
                        viewModel.listData1.value = ArrayList()
                        viewModel.listData1.value = viewModel.listData1.value
                        listener?.run { onResultCallback() }
                    }
            )
        },nextPage,statusValue)
    }



    fun getMoreClaimList2(status : Int,listener : OnSwipeRefreshResultListener?, nextPage : Int = 0){
        /*var statusValue = ""
        when(status){
            1->statusValue = "PENDING"
            2->statusValue = "COMPLETED"
        }*/
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        val accessToken = it.split("Bearer")[1]
                        val userId = JWT(accessToken).getClaim("userId").asInt()
                        if (userId != null){
                            ClaimServer.getMyPageSellerClaimList(OnServerListener { success, o ->
                                ServerCallbackUtil.executeByResultCode(success, o,
                                        successTask = {
                                            if(nextPage > 1){
                                                var idx = viewModel.listData2!!.value!!.size-1
                                                if(viewModel.listData2!!.value!![idx].id == 0){
                                                    viewModel.listData2!!.value!!.removeAt(idx)
                                                }
                                            }
                                            var data =  (o as BaseModel<*>).data as MyPageClaimSeller
                                            if(data.totalPages > 0 && (data.totalPages-1 > data.pageable.pageNumber)){
                                                var page = MyPageClaimSellerContent()
                                                viewModel.totalPageNum1 = data.totalPages
                                                viewModel.pageNum1 = data.pageable.pageNumber
                                                data.content.add(page)
                                            }
                                            if(data.content.size == 0 && data.pageable.pageNumber == 0){
                                                viewModel.emptyClaimVisible1.set(true)
                                            } else viewModel.emptyClaimVisible1.set(false)
                                            viewModel.mypageSellerClaimTotalCountTxt.set(data.totalElements.toString())

                                            viewModel.getListAdapter2()!!.items.addAll(data.content)
                                            viewModel.getListAdapter2().notifyDataSetChanged()
                                            listener?.run { onResultCallback() }
                                        },
                                        dataNotFoundTask = {
                                            if(CustomLog.flag)CustomLog.L("getMoreClaimList","dataNotFoundTask")
                                            viewModel.emptyClaimVisible2.set(true)
                                            viewModel.mypageSellerClaimTotalCountTxt.set("0")
                                            listener?.run { onResultCallback() }
                                        },
                                        failedTask = {
                                            if(CustomLog.flag)CustomLog.L("getMoreClaimList","failedTask")
                                            viewModel.emptyClaimVisible2.set(true)
                                            viewModel.mypageSellerClaimTotalCountTxt.set("0")
                                            listener?.run { onResultCallback() }
                                        },
                                        serverRuntimeErrorTask = {
                                            if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverRuntimeErrorTask")
                                            viewModel.emptyClaimVisible2.set(true)
                                            viewModel.mypageSellerClaimTotalCountTxt.set("0")
                                            listener?.run { onResultCallback() }
                                        },
                                        serverLoginErrorTask = {
                                            if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverLoginErrorTask")
                                            viewModel.emptyClaimVisible2.set(true)
                                            viewModel.mypageSellerClaimTotalCountTxt.set("0")
                                            listener?.run { onResultCallback() }
                                        },
                                        dataIsNull = {
                                            if(CustomLog.flag)CustomLog.L("getMoreClaimList","dataIsNull")
                                            viewModel.emptyClaimVisible2.set(true)
                                            viewModel.mypageSellerClaimTotalCountTxt.set("0")
                                            viewModel.listData2.value = ArrayList()
                                            viewModel.listData2.value = viewModel.listData2.value
                                            listener?.run { onResultCallback() }
                                        }
                                )
                            },it, userId, nextPage,"")
                        }
                    }
                }, invalidTokenTask = { ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin)) })

    }

    fun deleteClaim(productId : Long, inquiryId : Long, listener : OnServerListener){
        ClaimServer.deleteClaim(listener, productId, inquiryId)
    }

    fun deleteSellerClaim(userId : Long, id : Int, listener : OnServerListener){
        ClaimServer.deleteSellerClaim(listener, userId, id)
    }
}