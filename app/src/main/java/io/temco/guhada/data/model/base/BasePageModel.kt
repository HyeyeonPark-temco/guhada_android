package io.temco.guhada.data.model.base

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author park jungho
 * 19.07.31
 *
 * 영일님 response 기준 page model
 */
open class BasePageModel : Serializable {

    @SerializedName("pageable")
    var pageable : Pageable = Pageable()

    @SerializedName("pageSize")
    var pageSize : Int = 0

    @SerializedName("totalElements")
    var totalElements : Int = 0

    @SerializedName("totalPages")
    var totalPages : Int = 0

    @SerializedName("last")
    var last : Boolean = false

    @SerializedName("first")
    var first : Boolean = false

    @SerializedName("size")
    var size : Int = 0

    @SerializedName("number")
    var number : Int = 0

    @SerializedName("numberOfElements")
    var numberOfElements : Int = 0

    @SerializedName("empty")
    var empty : Boolean = false

    @SerializedName("sort")
    var sort : Sort = Sort()


    inner class Pageable {

        @SerializedName("pageSize")
        var pageSize : Int = 0

        @SerializedName("pageNumber")
        var pageNumber : Int = 0

        @SerializedName("offset")
        var offset : Int = 0

        @SerializedName("paged")
        var paged : Boolean = false

        @SerializedName("unpaged")
        var unpaged : Boolean = false

        @SerializedName("sort")
        var sort : Sort = Sort()

        override fun toString(): String {
            return "Pageable(pageSize=$pageSize, pageNumber=$pageNumber, offset=$offset, paged=$paged, unpaged=$unpaged, sort=$sort)"
        }

    }

    inner class Sort {

        @SerializedName("sorted =")
        var sorted = false

        @SerializedName("unsorted")
        var unsorted = false

        @SerializedName("empty")
        var empty = false

        override fun toString(): String {
            return "Sort(sorted=$sorted, unsorted=$unsorted, empty=$empty)"
        }

    }

}