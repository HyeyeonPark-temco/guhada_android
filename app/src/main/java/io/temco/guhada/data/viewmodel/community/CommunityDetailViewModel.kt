package io.temco.guhada.data.viewmodel.community

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.CommunityOrderType
import io.temco.guhada.common.enum.ImageUploadTarget
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.*
import io.temco.guhada.data.model.user.LikesModel
import io.temco.guhada.data.server.CommunityServer
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.CommunityDetailActivity
import io.temco.guhada.view.adapter.CommentListAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import java.util.*
import kotlin.collections.ArrayList

class CommunityDetailViewModel (val context : Context) : BaseObservableViewModel() {
    val repository = CommunityDetailRepository(this)

    var communityInfoList : ArrayList<CommunityInfo> = arrayListOf()
    var communityInfoMap : SortedMap<Int, CommunityCategory> = sortedMapOf()
    var communityCategoryMap : SortedMap<Int, SortedMap<Int,CommunityCategorySub>> = sortedMapOf()
    var totalCategoryCount = 0

    var mobileContentsList : ArrayList<CommunityMobileDetail> = arrayListOf()

    var userId = -1L
    var bbsId = 0L

    val UNIT_PER_PAGE = 10
    var mPage = 0
    var mFilterId = -1
    var mOrder = CommunityOrderType.DATE_DESC.type
    var mCategoryId = -1L
    var mCommunityResponse: MutableLiveData<CommunityBoard.CommunityResponse> = MutableLiveData()

    var communityDetail: MutableLiveData<CommunityDetail> = MutableLiveData()
    var commentList : MutableLiveData<ArrayList<Comments>> = MutableLiveData()
    var commentAdapter : CommentListAdapter? = null

    var info : CommunityInfo = CommunityInfo()

    init {
        repository.getCommunityInfo()
    }

    var initBookMarkData = false

    var userLoginCheck = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userLoginCheck)
        }

    var communityDetailClientPlatformWeb = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.communityDetailClientPlatformWeb)
        }

    var modifyComment = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.modifyComment)
        }
    var modifyIndex = -1

    var bbsBookMark = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            if (initBookMarkData) {
                field = value
                notifyPropertyChanged(BR.productBookMark)
            }
        }

    var bbsUserLike = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.bbsUserLike)
        }

    var bbsUserLikeCount = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.bbsUserLikeCount)
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


    var communityEventViewIndex = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.communityEventViewIndex)
        }

    var mRedirectDetailTask: (item: CommunityBoard) -> Unit = {}


    fun onClickBbsLike() {
        if(CustomLog.flag)CustomLog.L("onClickBbsLike","bbsBookMark.get()",bbsBookMark.get())
        if(bbsId != 0L){
            if (bbsUserLike.get())
                repository.deleteLike(-1, target = Type.BookMarkTarget.BBS.name, targetId = bbsId)
            else
                repository.saveLike(-1, target = Type.BookMarkTarget.BBS.name, targetId = bbsId)
        }
    }


    fun onClickCommentLike(index : Int, commentUserLike : Boolean, targetId : Long) {
        if(CustomLog.flag)CustomLog.L("onClickCommentLike","commentUserLike",index,commentUserLike,targetId)
        if (commentUserLike)
            repository.deleteLike(commentIndex = index, target = Type.BookMarkTarget.COMMENT.name, targetId = targetId)
        else
            repository.saveLike(commentIndex = index, target = Type.BookMarkTarget.COMMENT.name, targetId = targetId)
    }

    fun onClickBookMark(){
        if(initBookMarkData){
            if(bbsId != 0L){
                if (bbsBookMark.get())
                    repository.deleteBookMark(target = Type.BookMarkTarget.BBS.name, targetId = bbsId)
                else
                    repository.saveBookMark(target = Type.BookMarkTarget.BBS.name, targetId = bbsId)
            }
        }
    }


    fun onClickReplyAndModify(data : Comments, index : Int?) {
        commentReplyData = data
        modifyComment.set(data.isModify)
        if(!commentReplyData?.isModify!!){
            replyUserName.set(data.createUserInfo!!.nickname)
            if(context is CommunityDetailActivity){
                context.mBinding.layoutAppbar.setExpanded(false,true)
                context.mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.postDelayed({
                    CommonUtil.showKeyboard(context, context.mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail)
                },200)
            }
        }else{
            modifyIndex = index!!
            if(context is CommunityDetailActivity){
                context.mBinding.layoutAppbar.setExpanded(false,true)
                replyUserName.set("수정")
                if(!commentReplyData?.commentImageList.isNullOrEmpty()){
                    commentRegImage.set(commentReplyData!!.commentImageList[0].url)
                }
                if(context is CommunityDetailActivity){
                    context.mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.text = Editable.Factory.getInstance().newEditable(commentReplyData?.contents)
                }
            }
        }
    }


    fun getDetailData(){
        repository.getDetailData()
    }

    var totalPageCount = -1
    var currentPage = 0

    fun getCommentList(){
        if(totalPageCount == -1 || (totalPageCount-1 > currentPage)){
            currentPage += 1
            repository.getCommentList(currentPage,"DESC", null)
        }
    }

    fun getCommentInitList(listener: OnCallBackListener?){
        currentPage = 1
        commentList.value?.clear()
        commentAdapter?.mList?.clear()
        repository.getCommentList(currentPage,"DESC",listener)
    }

    fun getBookMark() {
        repository.getBookMark(Type.BookMarkTarget.BBS.name, bbsId)
    }

    fun postCommentData(listener: OnCallBackListener){
        if(modifyComment.get()){
            if(!TextUtils.isEmpty(commentRegImage.get())){
                if(!"http".equals(commentRegImage.get()!!.substring(0,4),true)){
                    repository.uploadImage(commentRegImage.get()!!,ImageUploadTarget.IMAGE_BBS_COMMENT.name,0, object : OnCallBackListener{
                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                            var data = value as ImageResponse
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "uploadImage postCommentData ",data.toString())
                            sendCommentData(commentReplyData, commentRegText.get(), data, listener)
                        }
                    })
                }else{
                    var image : ImageResponse ? = null
                    if(!commentReplyData!!.commentImageList.isNullOrEmpty()){
                        image = ImageResponse()
                        image.url = commentReplyData!!.commentImageList[0].url
                        image.fileName = commentReplyData!!.commentImageList[0].fileName
                        image.fileContentType = commentReplyData!!.commentImageList[0].fileType
                        image.fileSize = commentReplyData!!.commentImageList[0].fileSize
                        image.imageWidth = commentReplyData!!.commentImageList[0].width
                        image.imageHeight = commentReplyData!!.commentImageList[0].height
                    }
                    sendCommentData(commentReplyData, commentRegText.get(), image, listener)
                }
            }else{
                sendCommentData(commentReplyData, commentRegText.get(), null, listener)
            }
        }else{
            if(!TextUtils.isEmpty(commentRegImage.get())){
                repository.uploadImage(commentRegImage.get()!!,ImageUploadTarget.IMAGE_BBS_COMMENT.name,0, object : OnCallBackListener{
                    override fun callBackListener(resultFlag: Boolean, value: Any) {
                        if(resultFlag){
                            var data = value as ImageResponse
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "uploadImage postCommentData ",data.toString())
                            sendCommentData(commentReplyData, commentRegText.get(), data, listener)
                        }else{
                            CustomMessageDialog(message = "이미지 전송중 오류가 발생되었습니다.",
                                    cancelButtonVisible = false,
                                    confirmTask = {
                                        postCommentDataClear()
                                    }).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                        }
                    }
                })
            }else{
                sendCommentData(commentReplyData, commentRegText.get(), null, listener)
            }
        }
    }

    fun deleteDetail(listener: OnCallBackListener){
        repository.deleteDetail(bbsId, listener)
    }

    fun postCommentDataClear(){
        commentRegText.set("")
        commentRegImage.set("")
        commentBtnVisible.set(false)
        modifyComment.set(false)
        modifyIndex = -1
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
        if(data != null && data!!.isModify){
            repository.modifyCommentData(data.id, body, listener)
        }else {
            repository.postCommentData(body, listener)
        }
    }


    fun deleteComment(id : Long, index : Int, listener: OnCallBackListener){
        showLoadingDialog()
        repository.deleteComment(id, index, listener)
    }


    fun showLoadingDialog(){
        if(!(context as CommunityDetailActivity).getLoading().isShowing){
            context.getLoading().show()
        }
    }

    fun dismissLoadingDialog(){
        if((context as CommunityDetailActivity).getLoading().isShowing){
            context.getLoading().dismiss()
        }
    }


    fun onClickItem(item: CommunityBoard) = mRedirectDetailTask(item)

}

class CommunityDetailRepository(val viewModel: CommunityDetailViewModel){

    /**
     * 게시글 상세 가져오기
     */
    fun getDetailData(){
        var token = ""
        ServerCallbackUtil.callWithToken(task = { token = it},invalidTokenTask = {})
        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getBbsDetailToken", "getDetailData token", token)
        CommunityServer.getBbsDetailToken(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as CommunityDetail
                        viewModel.bbsUserLike.set(data.like)
                        viewModel.bbsUserLikeCount.set(data.likeCount)
                        viewModel.communityDetail.postValue(data)
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getBbsDetail dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getBbsDetail failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getBbsDetail userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getBbsDetail serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getBbsDetail dataIsNull ") }
            )
        }, token, viewModel.bbsId, CommonUtil.getUserIp())
    }


    /**
     * 댓글 목록 가져오기
     */
    fun getCommentList(page : Int, orderType : String, listener: OnCallBackListener?){
        var token = ""
        ServerCallbackUtil.callWithToken(task = { token = it},invalidTokenTask = {})
        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getCommentList", "getCommentList token", token)
        CommunityServer.getCommentListToken(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as CommentContent
                        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getCommentList", "totalElements", data.totalElements.toString())
                        var lastIndex = 0
                        if(!viewModel.commentList.value.isNullOrEmpty()){
                            lastIndex = viewModel.commentList.value!!.size-1
                            if(viewModel.commentList.value!![lastIndex].id < 0){
                                viewModel.commentList.value!!.removeAt(lastIndex)
                            }
                        }
                        viewModel.communityDetailCommentTotalElements.set(viewModel.communityDetail.value!!.commentCount)
                        viewModel.currentPage = data.pageable.pageNumber+1
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
                        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getCommentList", "currentPage", viewModel.currentPage)
                        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getCommentList", "totalPages", data.totalPages)
                        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getCommentList", "lastIndex", lastIndex)
                        if(data.totalPages > viewModel.currentPage){
                            var more = Comments()
                            more.id = -1
                            listTmp.add(more)
                        }
                        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getCommentList 1", "viewModel.commentList.value!!.size", viewModel.commentList.value?.size ?:0)
                        if(viewModel.commentList.value.isNullOrEmpty()){
                            viewModel.commentList.value = listTmp
                        }else{
                            viewModel.commentList.value!!.addAll(listTmp)
                            //viewModel.commentAdapter?.notifyItemRangeChanged(lastIndex,viewModel.commentList.value!!.size)
                        }
                        viewModel.commentAdapter?.setItems(viewModel.commentList.value!!)
                        viewModel.commentAdapter?.notifyDataSetChanged()
                        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity getCommentList 2", "viewModel.commentList.value!!.size", viewModel.commentList.value!!.size)
                        listener?.callBackListener(true,"")
                    },
                    dataNotFoundTask = { nullCommentData(false, "dataNotFoundTask",listener) },
                    failedTask = { nullCommentData(false, "failedTask",listener) },
                    userLikeNotFoundTask = { nullCommentData(false, "userLikeNotFoundTask",listener) },
                    serverRuntimeErrorTask = {nullCommentData(false, "serverRuntimeErrorTask",listener) },
                    dataIsNull = { nullCommentData(false, "dataIsNull",listener) }
            )
        }, token, viewModel.bbsId, page = page,orderType = orderType, unitPerPage = 10)
    }

    /**
     * 댓글 목록이 없는 경우
     */
    private fun nullCommentData(flag : Boolean, msg : String, listener: OnCallBackListener?){
        if(viewModel.commentList.value.isNullOrEmpty()){
            var listTmp : ArrayList<Comments> = arrayListOf()
            viewModel.commentList.value = listTmp
        }
        listener?.callBackListener(flag, msg)
    }


    /**
     * 댓글 작성하기
     */
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
                                        if(startIndex == null){
                                            viewModel.getCommentInitList(listener)
                                        }else{
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


    /**
     * 댓글 수정하기
     */
    fun modifyCommentData(id : Long, body : CommentResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        CommunityServer.modifyCommentData(OnServerListener { success, o ->
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
                                        if(startIndex == null){
                                            viewModel.getCommentInitList(listener)
                                        }else{
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
                        }, accessToken = it,id = id, body = body)
                    }
                }, invalidTokenTask = { viewModel.bbsBookMark.set(false) })
    }

    /**
     * 댓글의 id로 댓글 데이터 가져오기
     */
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
                        viewModel.communityDetailCommentTotalElements.set(viewModel.communityDetailCommentTotalElements.get()+1)
                        viewModel.commentAdapter?.mList!!.addAll(addIndex,listTmp)
                        //viewModel.commentAdapter?.notifyItemInserted(addIndex)
                        viewModel.commentAdapter?.notifyItemRangeChanged(addIndex,listTmp.size)
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


    /**
     * 댓글 삭제하기
     */
    fun deleteComment(id : Long, index : Int, listener: OnCallBackListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        CommunityServer.deleteComment(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<Any>).data
                                        if(CustomLog.flag) CustomLog.L("deleteComment successTask", value.toString())
                                        listener?.callBackListener(true,"")
                                    },
                                    dataNotFoundTask = { listener?.callBackListener(false,"dataNotFoundTask") },
                                    failedTask = { listener?.callBackListener(false,"failedTask") },
                                    userLikeNotFoundTask = { listener?.callBackListener(false,"userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener?.callBackListener(false,"serverRuntimeErrorTask") }
                            )
                        }, accessToken = it, id = id)
                    }
                }, invalidTokenTask = { listener?.callBackListener(false,"invalidTokenTask") })
    }


    /**
     * 작성글 삭제 하기
     */
    fun deleteDetail(id : Long, listener: OnCallBackListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        CommunityServer.deleteBbsData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<Any>).data
                                        if(CustomLog.flag) CustomLog.L("deleteComment successTask", value.toString())
                                        listener?.callBackListener(true,"")
                                    },
                                    dataNotFoundTask = { listener?.callBackListener(false,"dataNotFoundTask") },
                                    failedTask = { listener?.callBackListener(false,"failedTask") },
                                    userLikeNotFoundTask = { listener?.callBackListener(false,"userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener?.callBackListener(false,"serverRuntimeErrorTask") }
                            )
                        }, accessToken = it, id = id)
                    }
                }, invalidTokenTask = { listener?.callBackListener(false,"invalidTokenTask") })
    }

    /**
     * 이미지 등록
     */
    fun uploadImage(fileNm : String, cloudResourceList : String, index : Int, listener : OnCallBackListener){
        GatewayServer.uploadImagePath2(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as ImageResponse
                        if (CustomLog.flag) CustomLog.L("uploadImage", "uploadImage successTask ",data.toString())
                        data.index = null
                        listener.callBackListener(true,data)
                    },
                    dataNotFoundTask = { listener?.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener?.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener?.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener?.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = { listener?.callBackListener(false, "dataIsNull") }
            )
        },cloudResourceList,fileNm)
    }


    /**
     * 북마크 가져오기
     */
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


    /**
     * 북마크 설정
     */
    fun saveBookMark(target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    var bookMarkResponse = BookMarkResponse(target, targetId)
                    UserServer.saveBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if (CustomLog.flag) CustomLog.L("saveBookMark", "successTask")
                                    viewModel.bbsBookMark.set(true)
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, response = bookMarkResponse.getProductBookMarkRespose())
                }, invalidTokenTask = { })
    }


    /**
     * 북마크 삭제
     */
    fun deleteBookMark(target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    UserServer.deleteBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if (CustomLog.flag) CustomLog.L("deleteBookMark", "successTask")
                                    viewModel.bbsBookMark.set(false)
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, target = target, targetId = targetId)
                }, invalidTokenTask = { })
    }

    /**
     * 게시글, 댓글 좋아요 설정하기
     */
    fun saveLike(commentIndex : Int, target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    var response = LikesModel()
                    response.target = target
                    response.targetId = targetId
                    UserServer.saveLikes(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if (CustomLog.flag) CustomLog.L("saveLikes", "successTask")
                                    when(target){
                                        Type.BookMarkTarget.COMMENT.name->{
                                            viewModel.commentAdapter!!.mList[commentIndex].like = true
                                            viewModel.commentAdapter!!.mList[commentIndex].likeCount = viewModel.commentAdapter!!.mList[commentIndex].likeCount+1
                                            viewModel.commentAdapter!!.notifyItemChanged(commentIndex)
                                        }
                                        Type.BookMarkTarget.BBS.name->{
                                            viewModel.bbsUserLike.set(true)
                                            viewModel.bbsUserLikeCount.set(viewModel.bbsUserLikeCount.get()+1)
                                        }
                                    }
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, userId = CommonUtil.checkUserId(), response = response)
                }, invalidTokenTask = { })
    }


    /**
     * 게시글, 댓글 좋아요 삭제하기
     */
    fun deleteLike(commentIndex : Int, target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    var response = LikesModel()
                    response.target = target
                    response.targetId = targetId
                    UserServer.deleteLikes(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if (CustomLog.flag) CustomLog.L("deleteLikes", "successTask")
                                    when(target){
                                        Type.BookMarkTarget.COMMENT.name->{
                                            viewModel.commentAdapter!!.mList[commentIndex].like = false
                                            viewModel.commentAdapter!!.mList[commentIndex].likeCount = viewModel.commentAdapter!!.mList[commentIndex].likeCount-1
                                            viewModel.commentAdapter!!.notifyItemChanged(commentIndex)
                                        }
                                        Type.BookMarkTarget.BBS.name->{
                                            viewModel.bbsUserLike.set(false)
                                            viewModel.bbsUserLikeCount.set(viewModel.bbsUserLikeCount.get()-1)
                                        }
                                    }
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, target = target, targetId=targetId, userId = CommonUtil.checkUserId())
                }, invalidTokenTask = { })
    }


    /**
     * @author park jungho
     *
     * 커뮤니티 탭 정보 가져오기
     */
    fun getCommunityInfo(){
        viewModel.totalCategoryCount = 0
        CommunityServer.getCommunityAll(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategory>
                        for (info in data){
                            viewModel.communityInfoMap.put(info.id, info)
                            getCommunityCategory(info.id)
                        }
                        if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData successTask ",data.toString())
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        })
    }

    private fun getCommunityCategory(categoryId : Int){
        CommunityServer.getCommunityCategoryAll(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategorySub>
                        for (info in data){
                            if(!viewModel.communityCategoryMap.containsKey(categoryId) || viewModel.communityCategoryMap.get(categoryId).isNullOrEmpty()){
                                var map : SortedMap<Int, CommunityCategorySub> = sortedMapOf()
                                map.put(info.id, info)
                                viewModel.communityCategoryMap.put(categoryId,map)
                            }else{
                                viewModel.communityCategoryMap.get(categoryId)?.put(info.id, info)
                            }
                            getCommunityCategoryFilter(categoryId, info.id)
                        }
                        viewModel.totalCategoryCount += data.size
                        if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommunityCategory successTask ",viewModel.communityCategoryMap.toString())
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        },categoryId)
    }


    private  fun getCommunityCategoryFilter(communityId : Int, categoryId : Int){
        CommunityServer.getCategoryFilter(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategoryfilter>
                        viewModel.communityCategoryMap.get(communityId)?.get(categoryId)?.categoryFilterList?.addAll(data)
                        viewModel.totalCategoryCount -= 1
                        if(viewModel.totalCategoryCount == 0){
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "setDetailView getCommunityCategoryFilter 0 ",viewModel.communityCategoryMap.toString())
                            var list : java.util.ArrayList<CommunityInfo> = arrayListOf()
                            var communityKeys : Iterator<Int> = viewModel.communityCategoryMap.keys.iterator()
                            while (communityKeys.hasNext()){
                                var key = communityKeys.next()
                                var communityCategoryKeys : Iterator<Int> = viewModel.communityCategoryMap[key]!!.keys.iterator()
                                while (communityCategoryKeys.hasNext()){
                                    var cId = communityCategoryKeys.next()
                                    var info = viewModel.communityCategoryMap[key]!![cId]
                                    var tab = CommunityInfo()
                                    tab.communityId = info!!.communityId
                                    tab.communityCategoryId = info!!.id
                                    tab.communityCategory = viewModel.communityInfoMap[info!!.communityId]!!
                                    tab.communityCategorySub = info
                                    tab.communityName = viewModel.communityInfoMap[info!!.communityId]!!.name
                                    tab.communityCategoryName = info!!.name
                                    list.add(tab)
                                }
                            }
                            viewModel.communityInfoList = list
                        }else{
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommunityCategoryFilter totalCategoryCount ",viewModel.totalCategoryCount)
                        }
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        },categoryId)
    }

    /**
     * 게시글 목록 가져오기
     */
    fun getCommunityList() {
        // init filter id
        if (viewModel.mCategoryId > 0) {
            CommunityCriteria().apply {
                this.categoryId = viewModel.info.communityCategoryId.toLong()
                this.filterId = viewModel.mFilterId
                this.deleted = false
                this.inUse = true
                this.query = ""
                this.searchType = CommunityCriteria.SearchType.TITLE_CONTENTS.type
            }.let { criteria ->
                SearchServer.getCommunityBoardList(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                if (viewModel.mPage > 1) {
                                    val newList = (it.data as CommunityBoard.CommunityResponse).bbs
                                    viewModel.mCommunityResponse.value?.bbs?.addAll(newList)
                                    viewModel.mCommunityResponse.postValue(viewModel.mCommunityResponse.value)
                                } else {
                                    viewModel.mCommunityResponse.postValue(it.data as CommunityBoard.CommunityResponse)
                                }
                            })
                }, criteria = criteria, order = viewModel.mOrder, page = ++viewModel.mPage, unitPerPage = viewModel.UNIT_PER_PAGE)
            }
        } else {
            // 전체글 조회
            val jsonObject = JsonObject()
            jsonObject.addProperty("deleted", false)
            jsonObject.addProperty("inUse", true)
            jsonObject.addProperty("searchType", "CONTENTS")
            SearchServer.getCommunityBoardList(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (viewModel.mPage > 1) {
                                val newList = (it.data as CommunityBoard.CommunityResponse).bbs
                                viewModel.mCommunityResponse.value?.bbs?.addAll(newList)
                                viewModel.mCommunityResponse.postValue(viewModel.mCommunityResponse.value)
                            } else {
                                viewModel.mCommunityResponse.postValue(it.data as CommunityBoard.CommunityResponse)
                            }
                        })
            }, criteria = jsonObject, order = viewModel.mOrder, page = ++viewModel.mPage, unitPerPage = viewModel.UNIT_PER_PAGE)
        }

    }

}