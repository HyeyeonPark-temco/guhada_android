package io.temco.guhada.data.model.point

import io.temco.guhada.data.model.order.PaymentMethod

/**
 * 마이페이지 포인트 클래스
 * @author Hyeyeon Park
 * @since 2019.08.02
 */
class Point {
    var id: Long = 0
    var divisionType = ""
    var pointType = ""
    var name = ""
    var desc = ""
    var displayUnitType = ""
    var saveType = ""
    var saveRate: Double = 0.0
    var savePoint = 0
    var expirePeriod = 0
    var active = false
    var seller = false
    var sellerId: Long = 0

    var createdAt = "" // 2019-05-20T09:19:09
    var createdBy = ""
    var updatedAt = ""
    var updatedBy = ""

    var userLevelAddPointList = mutableListOf<UserLevelAddPoint>()
    var parentMethodSaveRateList = mutableListOf<PaymentMethodSaveRate>()

    inner class UserLevelAddPoint {
        var userLevelAddPointKey = UserLevelAddPointKey()
        var saveRate: Double = 0.0
        var savePrice = 0

        inner class UserLevelAddPointKey {
            var pointId: Long = 0
            var userLevel = ""
        }
    }

    inner class PaymentMethodSaveRate {
        var parentMethodSaveRateKey = PaymentMethodSaveRateKey()
        var saveRate: Double = 0.0

        inner class PaymentMethodSaveRateKey {
            var pointId: Long = 0
            var parentMethodCd = PaymentMethod()
        }
    }


}
