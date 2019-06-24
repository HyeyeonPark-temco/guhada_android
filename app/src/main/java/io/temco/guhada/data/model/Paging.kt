package io.temco.guhada.data.model

class Paging {
    var sort: Sort = Sort()
    var pageSize = 0
    var pageNumber = 0
    var offset = 0

    var paged: Boolean = false
    var unpaged: Boolean = false
}