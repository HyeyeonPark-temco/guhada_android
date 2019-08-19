package io.temco.guhada.data.model.seller

/**
 * 셀러 반품지 정보
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
class SellerAddress {
    var id : Long = -1
    var userId : Long = -1
    var sellerAddressType = ""
    var defaultAddress = true
    var name = ""
    var telephone = ""
    var zip = ""
    var address = ""
    var roadAddress = ""
    var detailAddress = ""
    var createdAt = ""
}
