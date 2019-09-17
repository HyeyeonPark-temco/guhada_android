package io.temco.guhada.data.model

/**
 * 사용 가능 쿠폰, 포인트 갯수 조회
 * @since 2019.09.17
 * @author Hyeyeon Park
 */
class AvailableBenefitCount {
    var userId = 0L
    var totalFreePoint = 0
    var totalPaidPoint = 0
    var totalDueSavePoint = 0
    var totalDueExpirePoint = 0
    var totalAvailCoupon = 0
    var totalDueExpireCoupon = 0
    var totalUsedCoupon = 0
}