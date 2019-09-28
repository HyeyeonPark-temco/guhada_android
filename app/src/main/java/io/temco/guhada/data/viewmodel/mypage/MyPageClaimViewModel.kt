package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.mypage.MyPageClaimAdapter

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
    val listData : MutableLiveData<ArrayList<MyPageClaim.Content>> = MutableLiveData()

    private var adapter : MyPageClaimAdapter
    fun getListAdapter() = adapter

    private var selectClaimStatusFilter = 0
    var selectedIndex = 0

    init {
        listData.value = arrayListOf()
        adapter = MyPageClaimAdapter(this,listData.value!!)
        repository.getList()
    }

    var mypageClaimTotalCountTxt = ObservableField<String>("0")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageClaimTotalCountTxt)
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
        listData.value?.clear()
        adapter.items?.run{ clear() }
        adapter.notifyDataSetChanged()
        repository.setInitData(selectClaimStatusFilter,listener)
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
            loading.hide()
            if(success){
                var data = (o as BaseModel<JsonObject>)
                if(data.resultCode in 200..400){
                    adapter.items.removeAt(itemIndex)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    fun getMoreCalimList(nextPage : Int) {
        repository.getMoreClaimList(selectClaimStatusFilter,null, nextPage)
    }



}



class MyPageCliamRepository (val viewModel : MyPageClaimViewModel) {
    // 메인 홈 list data

    fun getList() {
        setInitData(0,null)
    }

    fun setInitData(status : Int, listener : OnSwipeRefreshResultListener?) {
        getMoreClaimList(status, listener)
    }

    fun getMoreClaimList(status : Int,listener : OnSwipeRefreshResultListener?, nextPage : Int = 0){
        var statusValue = ""
        when(status){
            1->statusValue = "PENDING"
            2->statusValue = "COMPLETED"
        }
        ClaimServer.getMyPageClaimList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        if(nextPage > 1){
                            var idx = viewModel.listData!!.value!!.size-1
                            if(viewModel.listData!!.value!![idx].inquiry.id == 0L){
                                viewModel.listData!!.value!!.removeAt(idx)
                            }
                        }
                        var data =  (o as BaseModel<*>).data as MyPageClaim
                        if(data.totalPages > 0 && (data.totalPages-1 > data.pageable.pageNumber)){
                            var page = MyPageClaim().Content()
                            page.totalPages = data.totalPages
                            page.pageNumber = data.pageable.pageNumber
                            data.content.add(page)
                        }
                        if(data.content.size == 0 && data.pageable.pageNumber == 1){
                            viewModel.emptyClaimVisible1.set(true)
                        } else viewModel.emptyClaimVisible1.set(false)
                        viewModel.mypageClaimTotalCountTxt.set(data.totalElements.toString())

                        viewModel.getListAdapter()!!.items.addAll(data.content)
                        viewModel.getListAdapter().notifyDataSetChanged()
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
                        viewModel.listData.value = ArrayList()
                        viewModel.listData.value = viewModel.listData.value
                        listener?.run { onResultCallback() }
                    }
            )
        },nextPage,statusValue)
    }

    fun deleteClaim(productId : Long, inquiryId : Long, listener : OnServerListener){
        ClaimServer.deleteClaim(listener, productId, inquiryId)
    }
}