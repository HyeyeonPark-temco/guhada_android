package io.temco.guhada.data.model

/**
 * BookMark Count 조회 API response
 * @see io.temco.guhada.common.enum.BookMarkTarget
 * @author Hyeyeon Park
 * @since 2019.08.27
 */
class BookMarkCountResponse {
    var bookmarkTarget: String = ""
    var targetId: Long = 0L
    var bookmarkCount: Int = 0
}