package io.temco.guhada.data.model

import io.temco.guhada.common.util.CustomLog

data class MainPopup (
        val id : Int = 0,
        val agent : String = "",
        val kind : String = "",
        val eventTitle : String = "",
        val eventProgress : String = "",
        val eventStartDate : String = "",
        val eventEndDate : String = "",
        val imgUrl : String = "",
        val imgUrlM : String = "",
        val createDate : String = "",
        val detailPage : Boolean = false,
        val imgDetailUrl : Any? = null,
        val imgDetailUrlM : Any? = null,
        val detailPageLink : Any? = null,
        val detailPageUrl : Any? = null,
        val bgColor : Any? = null,
        val detailWidth : Any? = null,
        val detailHeight : Any? = null,
        val mobileAppLink : Any? = null){

    override fun toString(): String {
        if(CustomLog.flag)return "MainPopup(id=$id, agent='$agent', kind='$kind', eventTitle='$eventTitle', eventProgress='$eventProgress', eventStartDate='$eventStartDate', eventEndDate='$eventEndDate', imgUrl='$imgUrl', imgUrlM='$imgUrlM', createDate='$createDate', detailPage=$detailPage, imgDetailUrl=$imgDetailUrl, imgDetailUrlM=$imgDetailUrlM, detailPageLink=$detailPageLink, detailPageUrl=$detailPageUrl, bgColor=$bgColor, detailWidth=$detailWidth, detailHeight=$detailHeight, mobileAppLink=$mobileAppLink)"
        else return ""
    }
}