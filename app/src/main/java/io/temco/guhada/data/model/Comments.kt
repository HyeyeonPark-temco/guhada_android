package io.temco.guhada.data.model

import io.temco.guhada.data.model.base.BasePageModel
import io.temco.guhada.data.model.community.CreateUserInfo

class CommentContent : BasePageModel() {
    var content : ArrayList<Comments> = arrayListOf()

    override fun toString(): String {
        return "CommentContent(content=$content)"
    }
}

class Comments {
    var id = 0L
    var communityBbsId = 0L
    var originCommentId : Long? = null
    var originCreaterUserId : Long? = null
    var originCreaterUser = CreateUserInfo()
    var parentCommentId : Long? = null
    var contents :String =  ""
    var likeCount = 0
    var like = false
    var commentList : ArrayList<Comments> = arrayListOf()
    var createdTimestamp = 0L
    var currentTimestamp = 0L
    var userId = 0L
    var createUserInfo = CreateUserInfo()
    var commentImageList : ArrayList<CommentImageList> = arrayListOf()

    override fun toString(): String {
        return "Comments(id=$id, communityBbsId=$communityBbsId, originCommentId=$originCommentId, originCreaterUserId=$originCreaterUserId, originCreaterUser=$originCreaterUser, parentCommentId=$parentCommentId, contents='$contents', likeCount=$likeCount, like=$like, commentList=$commentList, createdTimestamp=$createdTimestamp, currentTimestamp=$currentTimestamp, userId=$userId, createUserInfo=$createUserInfo, commentImageList=$commentImageList)"
    }

}

class CommentImageList {
    var id = 0L
    var commentId = 0L
    var url = ""
    var priority = 0
    var width = 0
    var height = 0
    var fileName = ""
    var fileType = ""
    var fileSize = 0L
    var createdAt = arrayOf<Int>()
    var createdTimestamp = 0L

    override fun toString(): String {
        return "CommentImageList(id=$id, commentId=$commentId, url='$url', priority=$priority, width=$width, height=$height, fileName='$fileName', fileType='$fileType', fileSize=$fileSize, createdAt='$createdAt', createdTimestamp=$createdTimestamp)"
    }

}
