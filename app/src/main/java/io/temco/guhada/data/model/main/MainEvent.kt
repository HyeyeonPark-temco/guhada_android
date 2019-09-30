package io.temco.guhada.data.model.main

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
                     val eventData : String){

}