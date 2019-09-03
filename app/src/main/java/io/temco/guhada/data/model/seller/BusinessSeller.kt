package io.temco.guhada.data.model.seller

/**
 * 비즈니스 셀러 정보
 *
 * # Format
 * createdAt, updatedAt: 2019-04-05T03:28:21
 * @author Hyeyeon Park
 * @since 2019.09.03
 */
class BusinessSeller {
    var id = 0L
    var sellerUser = Seller()
    var companyName = ""
    var businessDivision = ""
    var corporationRegistrationNumber = ""
    var representativeName = ""
    var businessType = ""
    var businessItem = ""
    var mailorderRegistrationNumber = ""
    var notReportingType = ""
    var businessRegistrationUrl = ""
    var mailorderRegistrationUrl = ""
    var certificateSealUrl = ""
    var representativeBankCopyUrl = ""
    var entityRegistrationCertificateUrl = ""
    var createdAt = ""
    var updatedAt = ""
    var updatedBy: String? = null
    var mailorderBusiness = false
}
