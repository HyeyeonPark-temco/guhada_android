package io.temco.guhada.data.model.shippingaddress

import java.io.Serializable

/**
 * 배송지 정보 클래스
 * @author Hyeyeon Park
 */
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