package io.temco.guhada.data.model

import io.temco.guhada.common.util.CustomLog

/**
 * https://temcolabs.atlassian.net/browse/TECH-4936
 *
 */
class GuhadaNotiMessage {

    var scheme = ""
    var title : String = ""
    var message = ""
    var image : String ?  = ""

    override fun toString(): String {
        if(CustomLog.flag)return "GuhadaNotiMessage(scheme='$scheme', title='$title', message='$message', image='$image')"
        else return ""
    }

}