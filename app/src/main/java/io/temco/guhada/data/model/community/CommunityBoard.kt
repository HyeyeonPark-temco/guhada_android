package io.temco.guhada.data.model.community

import io.temco.guhada.common.util.CommonUtil
import org.joda.time.DateTime

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

    fun getDateStr(): String = DateTime(date).toString("MM.dd")
    fun getDateDiff(): String {
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val DAY_MS = HOUR_MS * 24
        val diffSeconds = (now - date) * 1000

        return when {
            diffSeconds < MINUTE_MS -> "조금 전"
            diffSeconds < HOUR_MS -> "${diffSeconds / MINUTE_MS}분 전"
            diffSeconds < DAY_MS -> "${diffSeconds / HOUR_MS}시간 전"
            else -> { // 24시간 이상 전
                val boardDate = DateTime(date)
                val boardYear = boardDate.year
                val currentYear = DateTime(now).year

                if (boardYear < currentYear)
                    boardDate.toString("yyyy년 MM월 dd일")
                else
                    boardDate.toString("MM월 dd일")
            }
        }
    }

    class CommunityResponse {
        var bbs = mutableListOf<CommunityBoard>()
        var totalCount = 0
    }
}
