package io.temco.guhada.data.model.naver

import com.google.gson.annotations.SerializedName

/**
 * 네아로 유저 클래스
 * @see NaverResponse
 * @author Hyeyeon Park
 */
class NaverUser {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var gender: String = ""
    var age: String = ""
    var birthday: String = ""

    @SerializedName("nickname")
    var nickName: String = ""

    @SerializedName("profile_image")
    var profileImage: String = ""
}