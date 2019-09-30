package io.temco.guhada.data.model

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.base.BasePageModel

/**
 * @author park jungho
 * 19.07.31
 *
 * 북마크 data
 */
class BookMark : BasePageModel() {

    @SerializedName("content")
    var content : MutableList<Content> = mutableListOf()

    @SerializedName("result")
    var result : String = ""

    @SerializedName("data")
    var data : String = ""

    @SerializedName("message")
    var message : String = ""

    @SerializedName("resultCode")
    var resultCode : Int = 0

    var isBookMarkSet : Boolean = false
        get() = !content.isNullOrEmpty()

    inner class Content {
        var id : Long = 0L
        var target : String = ""
        var targetId: Long = 0L
        var userId: Long = 0L
        var createdAt : String = ""

    }

}