package io.temco.guhada.data.model.seller

import io.temco.guhada.data.model.user.User
import java.io.Serializable

class Seller : Serializable {
    var id: Long = 0
    var profileImageUrl = ""
    var managerName = ""
    var managerEmail = ""
    var managerMobile = ""
    var managerTelephone = ""
    var fax = ""
    var calculationReceivingMethod = ""
    var bankCode = ""
    var accountNumber = ""
    var accountHolder = ""

    var storeName = "" // [19.08.27] 미사용 데이터
    var storeIntroduction = ""
    var claimTelephone = ""
    var createdAt = ""
    var updatedAt: String? = ""
    var updatedBy: String? = ""
    var user: User = User()

}

