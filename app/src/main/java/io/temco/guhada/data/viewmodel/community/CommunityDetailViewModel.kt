package io.temco.guhada.data.viewmodel.community

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.CommentContent
import io.temco.guhada.data.model.Comments
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.CommunityDetail
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.server.CommunityServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.CommentListAdapter

class CommunityDetailViewModel (val context : Context) : BaseObservableViewModel() {
    val repository = CommunityDetailRepository(this)

    var bbsId = 0L
    var communityDetail: MutableLiveData<CommunityDetail> = MutableLiveData()
    var commentList : MutableLiveData<ArrayList<Comments>> = MutableLiveData()
    var commentAdapter : CommentListAdapter? = null

    var info : CommunityInfo = CommunityInfo()

    var initBookMarkData = false
    var bbsBookMark = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            if (initBookMarkData) {
                field = value
                notifyPropertyChanged(BR.productBookMark)
            }
        }

    var totalPageCount = -1
    var currrentPage = 0
    var communityDetailCommentTotalElements = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.communityDetailCommentTotalElements)
        }

    fun onClickLike() {

    }

    fun onClickBookMark(){
        if(initBookMarkData){

        }
    }


    fun onClickCommentMore() {

    }

    fun getDetailData(){
        repository.getDetailData()
    }

    fun getCommentList(){
        if(totalPageCount == -1 || (totalPageCount-1 > currrentPage)){
            currrentPage += 1
            repository.getCommentList(currrentPage,"DESC")
        }
    }

    fun getBookMark() {
        repository.getBookMark(Type.BookMarkTarget.BBS.name, bbsId)
    }

}

class CommunityDetailRepository(val viewModel: CommunityDetailViewModel){

    fun getDetailData(){
        CommunityServer.getBbsDetail(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as CommunityDetail
                        viewModel.communityDetail.postValue(data)
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        }, viewModel.bbsId, CommonUtil.getUserIp())
    }

    fun getCommentList(page : Int, orderType : String){
        CommunityServer.getCommentList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as CommentContent
                        if (CustomLog.flag) CustomLog.L("getCommentList", "totalElements", data.totalElements.toString())
                        viewModel.communityDetailCommentTotalElements.set(data.totalElements)
                        viewModel.currrentPage = data.pageable.pageNumber+1
                        var listTmp : ArrayList<Comments> = arrayListOf()
                        for (vlu in data.content){
                            listTmp.add(vlu)
                            if(!vlu.commentList.isNullOrEmpty()){
                                listTmp.addAll(vlu.commentList)
                            }
                        }
                        if(viewModel.commentList.value.isNullOrEmpty()){
                            viewModel.commentList.value = listTmp
                        }else{
                            viewModel.commentList.value!!.addAll(listTmp)
                        }
                        /* for ((index, vlu) in data.content.withIndex()){
                             if (CustomLog.flag) CustomLog.L("getCommentList "+index, vlu.toString())
                         }*/
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommentList dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommentList failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommentList userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommentList serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommentList dataIsNull ") }
            )
        }, viewModel.bbsId, page = page,orderType = orderType, unitPerPage = 10)
    }

    fun getBookMark(target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    if (CustomLog.flag) CustomLog.L("getBookMark", "callWithToken", it)
                    var userId = -1
                    if (it != null) {
                        userId = JWT(it.substring(7, it.length)).getClaim("userId").asInt() ?: -1
                        UserServer.getBookMark(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        viewModel.initBookMarkData = true
                                        if (CustomLog.flag) CustomLog.L("getBookMark", "successTask")
                                        var value = (it as BaseModel<BookMark>).data
                                        if (!value.content.isNullOrEmpty()) {
                                            viewModel.bbsBookMark.set(value.isBookMarkSet)
                                        }
                                    },
                                    dataNotFoundTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) },
                                    failedTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) },
                                    userLikeNotFoundTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) },
                                    serverRuntimeErrorTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) }
                            )
                        }, accessToken = it, target = target, targetId = targetId, userId = userId)
                    }
                }, invalidTokenTask = { viewModel.bbsBookMark.set(false) })
    }

}