package io.temco.guhada.data.model.naver

import com.google.gson.annotations.SerializedName

/**
 * 네아로 Response 클래스
 * @see NaverUser
 * @author Hyeyeon Park
 */
class NaverResponse {
    var message: String = ""

    @SerializedName("resultcode")
    var resultCode: String = ""

    @SerializedName("response")
    var user: NaverUser = NaverUser()
}