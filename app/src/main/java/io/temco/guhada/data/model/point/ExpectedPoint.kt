package io.temco.guhada.data.model.point

import java.io.Serializable

/**
 * 적립 예상 포인트
 * @author Hyeyeon Park
 * @since 2019.08.29
 */
class ExpectedPoint : Serializable {
    var dueSaveType = ""
    var pointType = ""
    var freePoint = 0
    var paidPoint = 0
    var totalPoint = 0
}