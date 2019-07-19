package io.temco.guhada.data.model.review

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.Paging
import io.temco.guhada.data.model.Sort

class ReviewResponse {
    var content: MutableList<ReviewResponseContent> = mutableListOf()
    var sort: Sort = Sort()

    @SerializedName("pageable")
    var paging: Paging = Paging()

    var totalElements = 0
    var totalPages = 0
    var last = false
    var first = false
    var numberOfElements = 0
    var size = 0
    var number = 0
    var empty = false
}
