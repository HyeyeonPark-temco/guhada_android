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
    fun sendKochavaEvent(eventName: String) {
        if (mFlag)
            Tracker.Event(eventName).let { event ->
                Tracker.sendEvent(event)
            }
    }

}