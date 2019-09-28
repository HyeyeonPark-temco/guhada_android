package io.temco.guhada.common

import io.reactivex.subjects.PublishSubject
import io.temco.guhada.common.util.CustomLog

object EventBusHelper {
    val mSubject = PublishSubject.create<EventBusData>()

    /**
     * @exception : The exception was not handled due to missing onError handler in the subscribe() method call
     * @author Hyeyeon Park
     * @since 2019.09.28
     */
    fun sendEvent(requestData: EventBusData) {
        mSubject.onNext(requestData)
    }
}