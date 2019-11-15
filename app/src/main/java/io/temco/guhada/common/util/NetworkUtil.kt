package io.temco.guhada.common.util

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {

    fun isNetworkConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val wimaxInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX)

        var isConnected = false
        if (mobileInfo != null && mobileInfo.isConnected) isConnected = mobileInfo.isConnected
        if (wifiInfo != null && wifiInfo.isConnected) isConnected = wifiInfo.isConnected
        if (wimaxInfo != null && wimaxInfo.isConnected) isConnected = wimaxInfo.isConnected
        return isConnected
    }
}