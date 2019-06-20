package io.temco.guhada.data.model

import java.io.Serializable

class ShippingAddress : Serializable {
    var id : Long = 0
    var add = false
    var addressDefault = false
    var addressBasic = ""
    var addressDetail = ""
    var addressName = ""
    var message = ""
    var messageCode = ""
    var phone= ""
    var receiverName =""
    var roadAddress = ""
    var zipcode = ""
    var safetyNoUse = false
}
