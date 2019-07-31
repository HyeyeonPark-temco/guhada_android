package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.data.viewmodel.mypage.repository.MyPageCliamRepository
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
    private var repository = MyPageCliamRepository(context)
    private val handler : Handler = Handler(context.mainLooper)

    private val _listData : SingleLiveEvent<ArrayList<MyPageClaim.Content>> = repository.getList()
    val listData : LiveData<ArrayList<MyPageClaim.Content>> get() = _listData

    private val adapter = MyPageClaimAdapter(this,listData.value!!)
    fun getListAdapter() = adapter

    var selectedIndex = 0
    var selectClaimStatusFilter = 0


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
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onShippingMemoSelected ", "position -----",position)
        if (claimMessages.size > position && selectedIndex != position) {
            selectClaimStatusFilter = position
            val message = claimMessages[position]
            selectedStatusMessage.set(message)
            notifyPropertyChanged(BR.selectedStatusMessage)
            reloadRecyclerView(null)
        }
    }

    fun deleteClaimItem(itemIndex : Int, productId : Long, inquiryId : Long, loading : LoadingIndicatorUtil){
        repository.deleteClaim(productId,inquiryId, OnServerListener { success, o ->
            if(success){
                loading.hide()
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