package io.temco.guhada.common.util

import com.kochava.base.Tracker
import io.temco.guhada.BuildConfig

/**
 * Tracking 관련 Util
 * @see io.temco.guhada.common.enum.TrackingEvent
 * @author Hyeyeon Park
 */
object TrackingUtil {
    private var mFlag = BuildConfig.BUILD_TYPE.contentEquals("release")

    @JvmStatic
    fun sendKochavaEvent(eventName: String, value: String = "") {
        if (mFlag) Tracker.sendEvent(eventName, value)
    }

    @JvmStatic
    fun sendKochavaEvent(event: Tracker.Event){
        if (mFlag) Tracker.sendEvent(event)
    }
}