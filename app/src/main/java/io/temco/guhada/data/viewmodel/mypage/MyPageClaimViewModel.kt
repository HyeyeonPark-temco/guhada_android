package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
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

    var emptyClaimVisible = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyClaimVisible)
        }


    fun reloadRecyclerView(listener : OnSwipeRefreshResultListener?){
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "reloadRecyclerView ", "init -----")
        listData.value?.clear()
        adapter.items?.run{ clear() }
        adapter.notifyDataSetChanged()
        repository.setInitData(selectClaimStatusFilter,listener)
    }

    var claimMessages: MutableList<String> = mutableListOf("답변상태 : 전체","답변상태 : 미답변","답변상태 : 완료")
        @Bindable
        get() = field

    var selectedStatusMessage = ObservableField<String>("답변상태 : 전체") // 스피너 표시 메세지
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

    fun getMoreClaimList(status : Int,listener : OnSwipeRefreshResultListener?, nextPage : Int = 1){
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
                            viewModel.emptyClaimVisible.set(true)
                        } else viewModel.emptyClaimVisible.set(false)

                        viewModel.getListAdapter()!!.items.addAll(data.content)
                        viewModel.getListAdapter().notifyDataSetChanged()
                        listener?.run { onResultCallback() }
                    },
                    dataNotFoundTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","dataNotFoundTask")
                    },
                    failedTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","failedTask")
                    },
                    serverRuntimeErrorTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverRuntimeErrorTask")
                    },
                    serverLoginErrorTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverLoginErrorTask")
                    },
                    dataIsNull = {
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