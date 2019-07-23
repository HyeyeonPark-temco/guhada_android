package io.temco.guhada.data.model.user

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.model.user.UserProfile

/**
 * SNS 회원 정보 클래스
 * @see UserProfile
 * @author Hyeyeon Park
 */
class SnsUser : User() {
    @SerializedName("profileJson")
    var userProfile: UserProfile? = null

    var snsId: String? = null
    var type: String? = null
}