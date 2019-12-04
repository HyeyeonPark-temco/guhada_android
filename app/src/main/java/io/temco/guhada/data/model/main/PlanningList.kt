package io.temco.guhada.data.model.main

import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.event.EventListData


class PlanningList(index: Int,
                type: HomeType,
                val planningData : PlanningListData) : MainBaseModel(index, type,2){

    override fun toString(): String {
        if(CustomLog.flag)return "PlanningList(planningData=$planningData)"
        else return ""
    }

}

class PlanningHeader(index: Int,
                  type: HomeType,
                  val planningHeader : PlanningHeaderData) : MainBaseModel(index, type,2){

    override fun toString(): String {
        if(CustomLog.flag)return "PlanningHeader(planningHeader=$planningHeader)"
        else return ""
    }

}


data class PlanningHeaderData(
        val count : Int,
        val stateName : List<String>,
        val stateType : List<String>){

    override fun toString(): String {
        if(CustomLog.flag)return "PlanningHeaderData(count=$count, stateName=$stateName, stateType=$stateType)"
        else return ""
    }
}


class PlanningListData{
    var id = 0
    var eventTitle = ""
    var eventProgress = ""
    var eventStartDate = ""
    var eventEndDate = ""
    var imgUrl = ""
    var imgUrlM = ""
    var createDate = ""
    var detailPage = false
    var imgDetailUrl = ""
    var imgDetailUrlM = ""
    var detailPageLink : String ? = ""
    var detailPageUrl : String ? = ""
    var bgColor = ""
    var mobileAppLink = ""
}

