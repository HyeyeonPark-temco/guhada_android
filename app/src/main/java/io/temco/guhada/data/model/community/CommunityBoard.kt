package io.temco.guhada.data.model.community

import io.temco.guhada.common.util.CommonUtil

/**
 * 커뮤니티 게시글 model
 * @author Hyeyeon Park
 * @since 2019.08.21
 */
class CommunityBoard {
    var bbsId = 0L
    var categoryId = 0L
    var categoryFilterName = ""
    var is_use = true
    var is_delete = false

    // BOARD
    var title = ""
    var userName = ""
    var imageUrl = ""
    var contents = ""

    // DATE
    var now = 0L
    var date = 0L
    var newlyCreated = false

    // COUNT
    var likes = 0
    var views = 0
    var comments = 0

    fun getDateStr(): String = CommonUtil.convertTimeStampToDate(date)
    fun getDateDiff(): String = CommonUtil.convertTimeStampToDate(now)

    class CommunityResponse {
        var bbs = mutableListOf<CommunityBoard>()
        var totalCount = 0
    }
}
