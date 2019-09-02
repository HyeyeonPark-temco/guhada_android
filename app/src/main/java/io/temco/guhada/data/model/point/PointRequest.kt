package io.temco.guhada.data.model.point

import io.temco.guhada.data.model.order.OrderItemResponse

/**
 * 적립 예정 포인트 api 호출 request
 * @author Hyeyeon Park
 * @since 2019.08.29
 */
class PointRequest {
    var bundleList = mutableListOf<PointBundle>()
    var pointType = ActionType.BUY.type
    val serviceType = ServiceType.AOS.type

    inner class PointBundle {
        var bundlePrice = 0
        var orderProdList = mutableListOf<OrderItemResponse>()
    }

    enum class ServiceType(val type: String) {
        AOS("AOS_APP")
    }

    enum class ActionType(val type: String) {
        BUY("BUY"),
        TEXT_REVIEW("TEXT_REVIEW"),
        IMG_REVIEW("IMG_REVIEW")
    }

    enum class SaveType(val type: String) {
        DOWNLOAD("DOWNLOAD")
    }
}