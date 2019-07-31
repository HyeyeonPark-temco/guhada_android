package io.temco.guhada.common

import androidx.annotation.Nullable
import io.reactivex.subjects.PublishSubject

object EventBusHelper {
     val mSubject = PublishSubject.create<EventBusData>()

    fun sendEvent(requestData: EventBusData) {
        mSubject.onNext(requestData)
    }
}