package io.temco.guhada.common.util

import com.kochava.base.Tracker

/**
 * Tracking 관련 Util
 * @see io.temco.guhada.common.enum.TrackingEvent
 * @author Hyeyeon Park
 */
object TrackingUtil {
    private var mFlag = true

    @JvmStatic
    fun sendKochavaEvent(key: String, value: String = "") {
        if (mFlag) Tracker.sendEvent(key, value)
    }

    @JvmStatic
    fun sendKochavaEvent(event:Tracker.Event) {
        if (mFlag) Tracker.sendEvent(event)
    }
}