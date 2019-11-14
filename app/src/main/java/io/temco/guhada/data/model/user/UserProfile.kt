package io.temco.guhada.data.model.user

import java.io.Serializable

/**
 * SNS 회원 정보 클래스
 * @see SnsUser
 * @author Hyeyeon Park
 */
open class UserProfile : Serializable {
    var snsId: String? = ""
    var email: String? = ""
    var name: String? = ""
    var familyName: String? = ""
    var givenName: String? = ""
    var imageUrl: String? = ""
}