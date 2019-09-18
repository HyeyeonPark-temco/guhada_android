package io.temco.guhada.data.model.point

import java.io.Serializable

/**
 * 적립 예정 포인트 api 호출 response
 * @author Hyeyeon Park
 * @since 2019.08.29
 */
class ExpectedPointResponse : Serializable {
    var dueSavePointList = mutableListOf<ExpectedPoint>()
}