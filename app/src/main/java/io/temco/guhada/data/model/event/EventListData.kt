package io.temco.guhada.data.model.event

import io.temco.guhada.common.util.CustomLog

class EventListData {
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

    override fun toString(): String {
        if(CustomLog.flag)return "EventListData(id=$id, eventTitle='$eventTitle', eventProgress='$eventProgress', eventStartDate='$eventStartDate', eventEndDate='$eventEndDate', imgUrl='$imgUrl', imgUrlM='$imgUrlM', createDate='$createDate', detailPage=$detailPage, imgDetailUrl='$imgDetailUrl', imgDetailUrlM='$imgDetailUrlM', detailPageLink=$detailPageLink, detailPageUrl=$detailPageUrl, bgColor='$bgColor', mobileAppLink='$mobileAppLink')"
        else return ""
    }


    /*override fun toString(): String {
        if(CustomLog.flag)return "EventList(id=$id, eventTitle='$eventTitle', eventProgress='$eventProgress', eventStartDate='$eventStartDate', eventEndDate='$eventEndDate', imgUrl='$imgUrl', imgUrlM='$imgUrlM', createDate='$createDate', detailPage=$detailPage, imgDetailUrl='$imgDetailUrl', imgDetailUrlM='$imgDetailUrlM', detailPageLink=$detailPageLink)"
        else return ""
    }*/


}