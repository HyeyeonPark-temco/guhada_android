package io.temco.guhada.data.model

import android.text.TextUtils

class AppVersionCheck {
    var osType = ""
    var appVersion = ""
    var createAt = ""
    var updateAt = ""


    fun isUpdateApp(currentAppVersion : String) : Boolean {
        var flag = false
        if(TextUtils.isEmpty(appVersion) && TextUtils.isEmpty(currentAppVersion)){

        }
        return flag
    }
}