package io.temco.guhada.data.model.point

import io.temco.guhada.data.model.order.OrderItemResponse

/**
 * 적립 예정 포인트 api 호출 request
 * @author Hyeyeon Park
 * @since 2019.08.29
 */
class PointRequest {
    var bundleList = mutableListOf<PointBundle>()
    var pointType = SaveActionType.BUY.type
    val serviceType = ServiceType.AOS.type

    inner class PointBundle {
        var bundlePrice = 0
        var orderProdList = mutableListOf<OrderItemResponse>()
    }

    enum class ServiceType(val type: String) {
        AOS("AOS_APP")
    }

    /**
     * saveActionType BUY == BUY + FOLLOW
     */
    enum class SaveActionType(val type: String) {
        BUY("BUY"),
        FOLLOW("FOLLOW"),
        BEST_REVIEW("BEST_REVIEW"),
        USER_REGISTRATION("USER_REGISTRATION")
    }

    enum class SaveType(val type: String) {
        DOWNLOAD("DOWNLOAD")
    }
}