package io.temco.guhada.data.model.order

import java.io.Serializable

/**
 * 결제 수단 정보 클래스
 * @see Order
 * @author Hyeyeon Park
 */
class PaymentMethod : Serializable {
    var methodCode = ""
    var methodName = ""
}