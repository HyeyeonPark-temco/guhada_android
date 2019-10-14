package io.temco.guhada.data.model.seller

import com.google.gson.annotations.Expose
import io.temco.guhada.common.util.CustomLog

/**
 * 셀러 스토어 정보
 * @author Hyeyeon Park
 * @since 2019.09.19
 */
class SellerStore {
    var sellerId = 0L
    var nickname = ""
    var offlineStoreAddress = ""            // 오프라인 스토어
    var storeIntroduction = ""              // 스토어 소개
    var representativeName = ""             // 대표자
    var companyRegistrationNumber = ""      // 사업자 등록번호
    var mailorderRegistrationNumber = ""    // 통신판매업 번호
    var zip = ""
    var claimTelephone = ""                 // 고객센터

    var productCount = 0
    var followerCount = 0
    var sellingCount = 0
    var goodSatisfactionCount = 0
    var normalSatisfactionCount = 0
    var badSatisfactionCount = 0

    var followed = false
    var storeIntroductionDetail: String? = ""
    var businessHours: String? = ""
    var profileImageUrl = ""

    @Expose
    var isFollowing = true

    override fun toString(): String {
        if (CustomLog.flag) return "SellerStore(nickname='$nickname', offlineStoreAddress='$offlineStoreAddress', storeIntroduction='$storeIntroduction', representativeName='$representativeName', companyRegistrationNumber='$companyRegistrationNumber', mailorderRegistrationNumber='$mailorderRegistrationNumber', zip='$zip', claimTelephone='$claimTelephone', followerCount=$followerCount, sellingCount=$sellingCount, goodSatisfactionCount=$goodSatisfactionCount, normalSatisfactionCount=$normalSatisfactionCount, badSatisfactionCount=$badSatisfactionCount, followed=$followed, storeIntroductionDetail=$storeIntroductionDetail, businessHours=$businessHours)"
        else return ""
    }


}