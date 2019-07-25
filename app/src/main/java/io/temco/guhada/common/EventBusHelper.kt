package io.temco.guhada.common

import io.reactivex.subjects.PublishSubject

object EventBusHelper {
     val mSubject = PublishSubject.create<Int>()

    fun sendEvent(requestCode: Int) {
        mSubject.onNext(requestCode)
    }
}