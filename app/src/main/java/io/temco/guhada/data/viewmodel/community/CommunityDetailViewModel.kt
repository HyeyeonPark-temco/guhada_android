package io.temco.guhada.data.viewmodel.community

import android.content.Context
import android.text.TextUtils
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.TextUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.CommunityDetail
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.server.CommunityServer
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.CommunityDetailActivity
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

    var commentBtnVisible = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentBtnVisible)
        }

    var commentRegText = ObservableField<String>("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentRegText)
        }

    var replyUserName = ObservableField<String>("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.replyUserName)
        }

    var commentRegImage = ObservableField<String>("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentRegImage)
        }


    var commentReplyData : Comments? = null

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


    fun onClickReply(data : Comments) {
        commentReplyData = data
        replyUserName.set(data.createUserInfo.nickname)
        if(context is CommunityDetailActivity){
            context.mBinding.layoutAppbar.setExpanded(false,true)
            context.mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.postDelayed({
                CommonUtil.showKeyboard(context, context.mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail)
            },200)
        }
    }


    fun onClickCommentMore() {

    }

    fun getDetailData(){
        repository.getDetailData()
    }

    var totalPageCount = -1
    var currrentPage = 0

    fun getCommentList(){
        if(totalPageCount == -1 || (totalPageCount-1 > currrentPage)){
            currrentPage += 1
            repository.getCommentList(currrentPage,"DESC", null)
        }
    }

    fun getCommentInitList(listener: OnCallBackListener?){
        currrentPage = 1
        commentList.value!!.clear()
        repository.getCommentList(currrentPage,"DESC",listener)
    }

    fun getBookMark() {
        repository.getBookMark(Type.BookMarkTarget.BBS.name, bbsId)
    }

    fun postCommentData(listener: OnCallBackListener){
        if(!TextUtils.isEmpty(commentRegImage.get())){
            repository.uploadImage(commentRegImage.get()!!,0, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    var data = value as ImageResponse
                    if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "uploadImage postCommentData ",data.toString())
                    sendCommentData(commentReplyData, commentRegText.get(), data, listener)
                }
            })
        }else{
            sendCommentData(commentReplyData, commentRegText.get(), null, listener)
        }
    }

    fun postCommentDataClear(){
        commentRegText.set("")
        commentRegImage.set("")
        commentBtnVisible.set(false)
        postCommentReplyDataClear()
    }


    fun postCommentReplyDataClear(){
        commentReplyData = null
        replyUserName.set("")
    }

    fun sendCommentData(data : Comments?, contents : String?, imageList : ImageResponse?, listener: OnCallBackListener){
        var body = CommentResponse()
        body.communityBbsId = bbsId
        if(data != null){
            if(data?.originCommentId != null) body.originCommentId = data?.id!!
            else body.originCommentId = data.id
            if(data?.parentCommentId != null) body.parentCommentId = data?.parentCommentId!!
            else body.parentCommentId = data.id
        }

        if(contents != null) body.contents = contents!!
        if(imageList != null) body.imageList = arrayOf(imageList!!)

        repository.postCommentData(body, listener)
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


    fun getCommentList(page : Int, orderType : String, listener: OnCallBackListener?){
        CommunityServer.getCommentList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as CommentContent
                        if (CustomLog.flag) CustomLog.L("getCommentList", "totalElements", data.totalElements.toString())
                        var lastIndex = 0
                        if(!viewModel.commentList.value.isNullOrEmpty()){
                            lastIndex = viewModel.commentList.value!!.size-1
                            if(viewModel.commentList.value!![lastIndex].id < 0){
                                viewModel.commentList.value!!.removeAt(lastIndex)
                            }
                        }
                        viewModel.communityDetailCommentTotalElements.set(data.totalElements)
                        viewModel.currrentPage = data.pageable.pageNumber+1
                        var listTmp : ArrayList<Comments> = arrayListOf()
                        for (vlu in data.content){
                            listTmp.add(vlu)
                            var parentIndex = lastIndex + listTmp.size - 1
                            if(!vlu.commentList.isNullOrEmpty()){
                                for (replyData in vlu.commentList){
                                    replyData.parentIndex = parentIndex
                                    listTmp.add(replyData)
                                }
                            }
                        }
                        if (CustomLog.flag) CustomLog.L("getCommentList", "currrentPage", viewModel.currrentPage)
                        if (CustomLog.flag) CustomLog.L("getCommentList", "totalPages", data.totalPages)
                        if(data.totalPages > viewModel.currrentPage){
                            var more = Comments()
                            more.id = -1
                            listTmp.add(more)
                        }
                        if(viewModel.commentList.value.isNullOrEmpty()){
                            viewModel.commentList.value = listTmp
                        }else{
                            viewModel.commentList.value!!.addAll(listTmp)
                            viewModel.commentAdapter?.notifyItemChanged(lastIndex)
                        }
                        listener?.callBackListener(true,"")
                    },
                    dataNotFoundTask = { listener?.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener?.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener?.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener?.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = { listener?.callBackListener(false, "dataIsNull") }
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


    fun postCommentData(body : CommentResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        CommunityServer.postCommentData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<Any>).data
                                        if (CustomLog.flag) CustomLog.L("postCommentData", "value", value)
                                        var startIndex : Int ? = null
                                        if(viewModel.commentReplyData != null){
                                            startIndex = viewModel.commentReplyData?.parentIndex
                                            var size = 0
                                            if(startIndex != null && !viewModel.commentList.value?.get(startIndex)?.commentList.isNullOrEmpty()){
                                                size += viewModel.commentList.value?.get(startIndex)?.commentList!!.size
                                            }
                                            if(CustomLog.flag) CustomLog.L("postCommentData", "startIndex", startIndex?:"null","size",size)
                                            if(startIndex != null){
                                                for (i in (startIndex+size) downTo startIndex ){
                                                    if(CustomLog.flag) CustomLog.L("postCommentData", "i", i,viewModel.commentAdapter?.mList?.get(i)?.contents?:"null")
                                                    viewModel.commentAdapter?.mList?.removeAt(i)
                                                }
                                            }
                                        }
                                        //viewModel.getCommentInitList(listener)
                                        viewModel.commentAdapter?.notifyDataSetChanged()
                                        if(startIndex == null){
                                            viewModel.getCommentInitList(listener)
                                        }else{
                                            //viewModel.getCommentInitList(listener)
                                            if(viewModel.context is CommunityDetailActivity){
                                                viewModel.context.getHandler().postDelayed({
                                                    getCommentGetIdData(viewModel.commentReplyData?.parentCommentId!! ,startIndex, listener)
                                                },400)
                                            }
                                        }
                                    },
                                    dataNotFoundTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) },
                                    failedTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) },
                                    userLikeNotFoundTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) },
                                    serverRuntimeErrorTask = { viewModel.initBookMarkData = true; viewModel.bbsBookMark.set(false) }
                            )
                        }, accessToken = it, body = body)
                    }
                }, invalidTokenTask = { viewModel.bbsBookMark.set(false) })
    }

    private fun getCommentGetIdData(id : Long, addIndex : Int, listener: OnCallBackListener?){
        CommunityServer.getCommentIdData(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as Comments
                        var listTmp : ArrayList<Comments> = arrayListOf()
                        listTmp.add(data)
                        var parentIndex = addIndex
                        if(!data.commentList.isNullOrEmpty()){
                            if(CustomLog.flag) CustomLog.L("getCommentGetIdData", data.commentList.size)
                            for (replyData in data.commentList){
                                if(CustomLog.flag) CustomLog.L("getCommentGetIdData", replyData.toString())
                                if(CustomLog.flag) CustomLog.L("getCommentGetIdData", "replyData",
                                        replyData.contents,replyData.originCommentId?:"null",replyData.parentCommentId?:"null")
                                replyData.parentIndex = parentIndex
                                listTmp.add(replyData)
                            }
                        }
                        viewModel.commentAdapter?.mList!!.addAll(addIndex,listTmp)
                        //viewModel.commentAdapter?.notifyItemInserted(addIndex)
                        //viewModel.commentAdapter?.notifyItemRangeInserted(addIndex+listTmp.size-1,1)
                        listener?.callBackListener(true, "")
                    },
                    dataNotFoundTask = { listener?.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener?.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener?.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener?.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = { listener?.callBackListener(false, "dataIsNull") }
            )
        }, id = id)
    }


    fun uploadImage(fileNm : String, index : Int, listener : OnCallBackListener){
        GatewayServer.uploadImage(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as ImageResponse
                        if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "uploadImage successTask ",data.toString())
                        data.index = null
                        listener.callBackListener(true,data)
                    },
                    dataNotFoundTask = { listener?.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener?.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener?.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener?.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = { listener?.callBackListener(false, "dataIsNull") }
            )
        },"REVIEW",fileNm)

    }

}