package io.temco.guhada.data.model.main

import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.event.EventListData


class EventList(index: Int,
                type: HomeType,
                val eventData : EventListData) : MainBaseModel(index, type,2){

    override fun toString(): String {
        if(CustomLog.flag)return "EventList(eventData=$eventData)"
        else return ""
    }

}

class EventHeader(index: Int,
                type: HomeType,
                val eventHeader : EventHeaderData) : MainBaseModel(index, type,2){

    override fun toString(): String {
        if(CustomLog.flag)return "EventHeader(eventHeader=$eventHeader)"
        else return ""
    }

}


data class EventHeaderData(
        val count : Int,
        val stateName : List<String>,
        val stateType : List<String>){

    override fun toString(): String {
        if(CustomLog.flag)return "EventHeaderData(count=$count, stateName=$stateName, stateType=$stateType)"
        else return ""
    }
}


