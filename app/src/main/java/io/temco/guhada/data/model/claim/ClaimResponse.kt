package io.temco.guhada.data.model.claim

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.Paging
import io.temco.guhada.data.model.Sort

/**
 * 상품 문의 Response 클래스
 * @author Hyeyeon Park
 */
class ClaimResponse {
    var totalPages = 0
    var totalElements = 0
    var numberOfElements = 0
    var size = 0
    var number = 0

    var content: MutableList<Claim> = mutableListOf()
    var pageable: Paging = Paging()
    var sort: Sort = Sort()
    var first = false

    @SerializedName("last")
    var last = false

    @SerializedName("empty")
    var empty = false

}