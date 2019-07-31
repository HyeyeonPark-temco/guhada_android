package io.temco.guhada.common

import java.io.Serializable

data class EventBusData (var requestCode: Int,
                         var data : Any?) : Serializable