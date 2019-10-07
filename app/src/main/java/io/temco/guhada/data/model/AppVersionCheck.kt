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
            if(!TextUtils.isEmpty(appVersion) && !TextUtils.isEmpty(currentAppVersion)){
                var appVersionSplit = appVersion.split(".")
                var version = (1000000 * appVersionSplit[0].toInt()) + (10000 * appVersionSplit[1].toInt()) + (100 * appVersionSplit[2].toInt())
                var appcVersionSplit = currentAppVersion.split(".")
                var cVersion = (1000000 * appcVersionSplit[0].toInt()) + (10000 * appcVersionSplit[1].toInt()) + (100 * appcVersionSplit[2].toInt())
                if(version < cVersion) flag = true
            }
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
            flag = false
        }
        return flag
    }


}