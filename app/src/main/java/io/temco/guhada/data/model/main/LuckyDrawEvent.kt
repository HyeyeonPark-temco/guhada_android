package io.temco.guhada.data.model.main

import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.event.LuckyDrawList
import io.temco.guhada.data.model.event.LuckyDrawTitleList


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 첫 이벤트 데이터
 */
class LuckyDrawTitle(index: Int,
                     type: HomeType,
                     val eventTile : ArrayList<LuckyDrawTitleList>,
                     val eventTime : LuckyDrawTimeData,
                     var expiredTimeLong : Long,
                     var displayTime : Long) : MainBaseModel(index, type,2){
    override fun toString(): String {
        if(CustomLog.flag)return "LuckyDrawTitle(expiredTimeLong=$expiredTimeLong, eventTile=$eventTile, eventTime=$eventTime)"
        else return ""
    }
}


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 첫 이벤트 데이터
 */
class LuckyDrawEvent(index: Int,
                     type: HomeType,
                     val eventData :LuckyDrawList,
                     var endTime : Long,
                     var expiredTimeLong : Long) : MainBaseModel(index, type,2){

    override fun toString(): String {
        if(CustomLog.flag)return "LuckyDrawEvent(eventData=$eventData, endTime=$endTime, expiredTimeLong=$expiredTimeLong)"
        else return ""
    }
}



/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 첫 이벤트 데이터
 */
class LuckyDrawEventFooter(index: Int,
                     type: HomeType) : MainBaseModel(index, type,2)


data class LuckyDrawTimeData(
        val now : Long,
        val remainedTimeForStart : Int,
        val remainedTimeForEnd : Int,
        val dealId : Long,
        val isVisible : Int){
    override fun toString(): String {
        if(CustomLog.flag)return "LuckyDrawTimeData(now=$now, remainedTimeForStart=$remainedTimeForStart, remainedTimeForEnd=$remainedTimeForEnd, dealId=$dealId, isVisible=$isVisible)"
        else return ""
    }
}