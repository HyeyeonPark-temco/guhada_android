package io.temco.guhada.data.model.point

import io.temco.guhada.data.model.order.OrderItemResponse

/**
 * 적립 예정 포인트 조회 Parameter
 * @author Hyeyeon Park
 * @since 2019.09.18
 */
class PointProcessParam {
    var consumptionPoint = 0
    var consumptionType = PointConsumption.BUY.type
    var pointType = PointSave.BUY.type
    var serviceType = PointRequest.ServiceType.AOS.type
    var bundleList = mutableListOf<PointBundle>()

    enum class PointConsumption(val type: String) {
        BUY("BUY"), PRODUCT_OPTION("PRODUCT_OPTION")
    }

    enum class PointSave(val type: String) {
        BUY("BUY"), TEXT_REVIEW("TEXT_REVIEW"), IMG_REVIEW("IMG_REVIEW"), BBS("BBS"), BBS_COMMENT("BBS_COMMENT"), PRODUCT_OPTION("PRODUCT_OPTION"), PAID_POINT("PAID_POINT"),
        REGISTRATION("REGISTRATION"), EMAIL_RECEIVE("EMAIL_RECEIVE"), MARKETING("MARKETING"), REVIEW("REVIEW"), FIRST_ORDER("FIRST_ORDER")
    }

    /**
     * orderProdList
     * - required: dcategoryId, lcategoryId, mcategoryId, scategoryId, dealId, discountPrice, productPrice, orderOptionList
     * @author Hyeyeon Park
     * @since 2019.09.18
     */
    class PointBundle {
        var bundlePrice = 0
        var orderProdList = mutableListOf<OrderItemResponse>()
    }
}