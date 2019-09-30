package io.temco.guhada.data.model

import io.temco.guhada.data.model.base.BasePageModel
import io.temco.guhada.data.model.community.CreateUserInfo
import java.io.Serializable

class CommentContent : BasePageModel() {
    var content : ArrayList<Comments> = arrayListOf()


}

class Comments : Serializable {
    var id = 0L
    var communityBbsId = 0L
    var originCommentId : Long? = null
    var originCreaterUserId : Long? = null
    var originCreaterUser : CreateUserInfo? = null
    var parentCommentId : Long? = null
    var contents :String =  ""
    var likeCount = 0
    var like = false
    var commentList : ArrayList<Comments> = arrayListOf()
    var createdTimestamp = 0L
    var currentTimestamp = 0L
    var userId = 0L
    var createUserInfo : CreateUserInfo? = null
    var commentImageList : ArrayList<CommentImageList> = arrayListOf()
    var delete = false
    var parentIndex : Int? = null // 대댓글에서 갱신하기 위한 상위 댓글 정보
    var isModify = false // 게시글 상세 댓글 리스트에서 수정을 판단하기 위한 정보


}


class CommentImageList : Serializable {
    var id = 0L
    var commentId = 0L
    var url = ""
    var priority = 0
    var width = 0
    var height = 0
    var fileName = ""
    var fileType = ""
    var fileSize = 0L
    var createdAt = Any()
    var createdTimestamp = 0L



}
