package io.temco.guhada.data.model

/**
 * 페이징 정보 클래스
 * @see ClaimResponse
 * @see io.temco.guhada.data.model.review.ReviewResponse
 * @author Hyeyeon Park
 */
class Paging {
    var sort: Sort = Sort()
    var pageSize = 0
    var pageNumber = 0
    var offset = 0

    var paged: Boolean = false
    var unpaged: Boolean = false
}