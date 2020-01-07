package io.temco.guhada.data.model.community

import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.common.util.DateUtil.checkNewly
import org.joda.time.DateTime

/**
 * 커뮤니티 게시글 model
 * @author Hyeyeon Park
 * @since 2019.08.21
 */
data class CommunityBoard(var contents: String = "") {
    var bbsId = 0L
    var categoryId = 0L
    var categoryFilterName: String? = null
    var is_use = true
    var is_delete = false

    // BOARD
    var title = ""
    var userName = ""
    var imageUrl = ""
//    var contents = ""

    // DATE
    var now = 0L
    var date = 0L

    val newlyCreated
        get() = checkNewly(now, date, true)

    // COUNT
    var likes = 0
    var views = 0
    var comments = 0

    fun getDateStr(): String = DateTime(date).toString("MM.dd")
    fun getDateDiff(): String = DateUtil.getDateDiff(now = now, date = date)

    class CommunityResponse {
        var bbs = mutableListOf<CommunityBoard>()
        var totalCount = 0
    }


}
