package io.temco.guhada.data.model

/**
 * 택배사 정보 클래스
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
class ShippingCompany {
    enum class Type(val type: String) {
        DOMESTIC("DOMESTIC"),
        OVERSEA("OVERSEA")
    }

    var code: String = ""
    var name: String = ""
    var type: String = ""
}