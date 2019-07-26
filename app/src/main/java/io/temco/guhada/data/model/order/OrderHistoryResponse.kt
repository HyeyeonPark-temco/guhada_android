package io.temco.guhada.data.model.order

/**
 * 주문 조회 API response 클래스
 * @author Hyeyeon Park
 * @since 2019.07.26
 */
class OrderHistoryResponse {
    var page = 0
    var count = 0
    var totalPage = 0
    var orderItemList = mutableListOf<PurchaseOrder>()
}