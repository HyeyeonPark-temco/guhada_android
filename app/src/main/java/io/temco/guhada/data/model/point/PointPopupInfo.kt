package io.temco.guhada.data.model.point

/**
 * 구매확정, 리뷰작성, 사이즈등록 시 포인트 팝업 정보
 * 구매확정: savedPoint, message, dueSavedPoint
 * 리뷰작성: savedPoint, message, totalFreePoint
 * 사이즈등록: savedPoint, message, totalFreePoint
 *
 * @author Hyeyeon Park
 * @since 2019.10.23
 */
class PointPopupInfo {
    var savedPoint = 0
    var dueSavedPoint = 0
    var totalFreePoint = 0
    var message = ""
}