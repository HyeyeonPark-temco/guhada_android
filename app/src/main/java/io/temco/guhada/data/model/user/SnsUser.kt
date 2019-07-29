package io.temco.guhada.data.model.user

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.model.user.UserProfile
import java.io.Serializable

/**
 * SNS 회원 정보 클래스
 * @see UserProfile
 * @author Hyeyeon Park
 */
class SnsUser : User(), Serializable {
    @SerializedName("profileJson")
    var userProfile: UserProfile? = UserProfile()

    var snsId: String? = ""
    var snsType: String? = ""
    val profileJsonString = "string"
}