package io.temco.guhada.common

import io.reactivex.subjects.PublishSubject

object EventBusHelper {
     val mSubject = PublishSubject.create<EventBusData>()

    fun sendEvent(requestData: EventBusData) {
        mSubject.onNext(requestData)
    }
}