package io.temco.guhada.data.model.event

import io.temco.guhada.common.util.CustomLog
import java.io.Serializable

class LuckyEvent {
    var titleList = arrayListOf<LuckyDrawTitleList>()
    var luckyDrawList = arrayListOf<LuckyDrawList>()

    override fun toString(): String {
        if(CustomLog.flag)return "LuckyEvent(titleList=$titleList, luckyDrawList=$luckyDrawList)"
        else return ""
    }
}

class LuckyDrawTitleList{
    var titleImageUrl = ""
    var requestFromAt = 0L
    var title = ""

    override fun toString(): String {
        if(CustomLog.flag)return "LuckyDrawTitleList(titleImageUrl='$titleImageUrl', requestFromAt=$requestFromAt, title='$title')"
        else return ""
    }
}


enum class Status(val code :String, val label: String){
    START("START", "응모하기"),
    NORMAL("NORMAL", "오픈"), // 시작시간전 NORMAL
    READY("READY", "오픈"),  // 시작시간 24이내가 되면 READY
    OUT_OF_TIME("OUT_OF_TIME", "응모 마감"),
    REQUESTED ("REQUESTED ", "응모 완료"),
    WINNER_ANNOUNCEMENT("WINNER_ANNOUNCEMENT", "당첨자 발표")
}

class LuckyDrawList : Serializable{
    var dealId = 0L
    var title = ""
    var sellPrice = 0L
    var discountPrice = 0L
    var discountRate = 0
    var imageUrl = ""
    var now = 0L
    var requestFromAt = 0L
    var requestToAt = 0L
    var remainedTimeForStart = 0
    var remainedTimeForEnd = 0
    var winnerAnnouncementAt = 0L
    var remainedTimeForWinnerAnnouncement = 0L
    var winnerBuyFromAt = 0L
    var winnerBuyToAt = 0L
    var statusCode = ""
    var statusText = ""
    var isDetailShow = false

    override fun toString(): String {
        if(CustomLog.flag)return "LuckyDrawList(dealId=$dealId, title='$title', sellPrice=$sellPrice, discountPrice=$discountPrice, discountRate=$discountRate, imageUrl='$imageUrl', now=$now, requestFromAt=$requestFromAt, requestToAt=$requestToAt, remainedTimeForStart=$remainedTimeForStart, remainedTimeForEnd=$remainedTimeForEnd, winnerAnnouncementAt=$winnerAnnouncementAt, remainedTimeForWinnerAnnouncement=$remainedTimeForWinnerAnnouncement, winnerBuyFromAt=$winnerBuyFromAt, winnerBuyToAt=$winnerBuyToAt, statusCode='$statusCode', statusText='$statusText')"
        else return ""
    }

}