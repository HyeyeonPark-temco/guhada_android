package io.temco.guhada.data.model.main

import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 첫 이벤트 데이터
 */
class MainEvent(index: Int,
                type: HomeType,
                val eventList : ArrayList<EventData>) : MainBaseModel(index, type,2){

}


data class EventData(val id : Int,
                     val imgPath : String,
                     val imgRes : Int,
                     val title : String,
                     val desc1 : String,
                     val desc2 : String,
                     val eventType : Int,
                     val eventData : String)


data class MainBanner(val id : Int,
                      val MainBanner : Boolean,
                      val title : String,
                      val largeImageUrl : String,
                      val mediumImageUrl : String,
                      val mobileImageUrl : String,
                      val communityImageUrl : String,
                      val imgRes : Int,
                      val link : String,
                      val backgroundColor : String,
                      val mobileAppLink : String){
    override fun toString(): String {
        if(CustomLog.flag)return "MainBanner(id=$id, MainBanner=$MainBanner, title='$title', largeImageUrl='$largeImageUrl', mediumImageUrl='$mediumImageUrl', mobileImageUrl='$mobileImageUrl', communityImageUrl='$communityImageUrl', imgRes=$imgRes, link='$link', backgroundColor='$backgroundColor', mobileAppLink='$mobileAppLink')"
        else return ""
    }
}