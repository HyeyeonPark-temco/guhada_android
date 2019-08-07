package io.temco.guhada.data.model

/**
 * 주문 취소 신청 클래스
 * @author Hyeyeon Park
 * @since 2019.08.07
 */
class CancelRequest {
    var cancelReason = ""
    var cancelReasonDetail = ""
    var orderProdGroupId: Long = 0
    var quantity = 0
}
