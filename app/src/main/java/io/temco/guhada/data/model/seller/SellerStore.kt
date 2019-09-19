package io.temco.guhada.data.model.seller

/**
 * 셀러 스토어 정보
 * @author Hyeyeon Park
 * @since 2019.09.19
 */
class SellerStore {
    var nickname = ""
    var offlineStoreAddress = ""
    var storeIntroduction = ""
    var representativeName = ""
    var companyRegistrationNumber = ""
    var mailorderRegistrationNumber = ""
    var zip = ""
    var claimTelephone = ""

    var followerCount = 0
    var sellingCount = 0
    var goodSatisfactionCount = 0
    var normalSatisfactionCount = 0
    var badSatisfactionCount = 0

    var followed = false
    var storeIntroductionDetail: String? = ""
    var businessHours: String? = ""
}