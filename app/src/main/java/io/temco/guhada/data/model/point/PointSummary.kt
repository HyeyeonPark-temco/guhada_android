package io.temco.guhada.data.model.point

/**
 * 포인트 Summary 정보 클래스
 * 마이페이지 포인트 상단에서 사용
 * @author Hyeyeon Park
 * @since 2019.08.02
 */
class PointSummary {
    var userId: Long = 0
    var totalFreePoint = 0      // 무료 충전 포인트
    var totalPaidPoint = 0      // 유료 충전 포인트
    var totalDueSavePoint = 0   // 적립 예정 포인트
    var totalDueExpirePoint = 0 // 적립 포인트
}