package io.temco.guhada.data.model.seller

import com.google.gson.annotations.Expose
import io.temco.guhada.data.model.user.User
import java.io.Serializable

class Seller : Serializable {
    var id: Long = 0
    var managerName: String? = ""
    var managerEmail: String? = ""
    var managerMobile: String? = ""
    var managerTelephone: String? = ""
    var fax: String? = ""
    var calculationReceivingMethod: String? = ""
    var bankCode: String? = ""
    var accountNumber: String? = ""
    var accountHolder: String? = ""

    var storeName: String? = "" // [19.08.27] 미사용 데이터
    var storeIntroduction: String? = ""

    var claimTelephone: String? = ""
//    var createdAt: Long = 0L
//    var updatedAt: Long = 0L
    var updatedBy: String? = ""
    var user: User = User()

    @Expose
    var isFollowing = true

}

