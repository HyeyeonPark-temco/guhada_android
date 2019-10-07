package io.temco.guhada.data.model.shippingaddress

import io.temco.guhada.common.util.CustomLog
import java.io.Serializable

/**
 * 배송지 정보 클래스
 * @author Hyeyeon Park
 */
class ShippingAddress : Serializable {
    var id: Long = 0
    var add = false
    var addressDefault = false
    var addressBasic = ""
    var addressDetail = ""
    var addressName = ""
    var message = ""
        get() {
            return if (field == null || field == "false") ""
            else field
        }
    var messageCode = ""
    var phone = ""
    var receiverName = ""
    var roadAddress = ""
    var zipcode = ""
    var safetyNoUse = false

    override fun toString(): String {
        if(CustomLog.flag)return "ShippingAddress(id=$id, add=$add, message=$message,addressDefault=$addressDefault, addressBasic='$addressBasic', addressDetail='$addressDetail', addressName='$addressName', messageCode='$messageCode', phone='$phone', receiverName='$receiverName', roadAddress='$roadAddress', zipcode='$zipcode', safetyNoUse=$safetyNoUse)"
        else return ""
    }


}