package io.temco.guhada.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentResponse {

    @SerializedName("communityBbsId")
    var communityBbsId = 0L
    @SerializedName("contents")
    var contents = ""

    @Expose
    var id : Long? = null

    @Expose
    var originCommentId : Long? = null

    @Expose
    var parentCommentId : Long? = null

    @Expose
    var imageList : Array<ImageResponse>? = null


}