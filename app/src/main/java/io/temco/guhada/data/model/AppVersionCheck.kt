package io.temco.guhada.data.model

import android.text.TextUtils
import io.temco.guhada.common.util.CustomLog
import java.lang.Exception

class AppVersionCheck {
    var osType = ""
    var appVersion = ""
    var createAt = ""
    var updateAt = ""

    fun isUpdateApp(currentAppVersion : String) : Boolean {
        var flag = false
        try{
            if("2019-09-27 10:45:29" != updateAt && !TextUtils.isEmpty(appVersion) && !TextUtils.isEmpty(currentAppVersion)){
                var serverVersionSplit = appVersion.split(".")
                var serverVrsion = (1000000 * serverVersionSplit[0].toInt()) + (10000 * serverVersionSplit[1].toInt()) + (100 * serverVersionSplit[2].toInt())
                var appVersionSplit = currentAppVersion.split(".")
                var appVersion = (1000000 * appVersionSplit[0].toInt()) + (10000 * appVersionSplit[1].toInt()) + (100 * appVersionSplit[2].toInt())
                if(serverVrsion > appVersion) flag = true
            }
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
            flag = false
        }
        return flag
    }

    override fun toString(): String {
        if(CustomLog.flag)return "AppVersionCheck(osType='$osType', appVersion='$appVersion', createAt='$createAt', updateAt='$updateAt')"
        else return ""
    }


}