package io.temco.guhada.data.model

import com.google.gson.annotations.SerializedName

/**
 * JWT 토큰 정보 클래스
 * @author Hyeyeon Park
 */
class Token {
    @SerializedName(value = "accessToken", alternate = ["access_token"])
    var accessToken: String? = null

    @SerializedName(value = "refreshToken", alternate = ["refresh_token"])
    var refreshToken: String? = null

    @SerializedName(value = "expiresIn", alternate = ["expires_in"])
    var expiresIn: Long = 0L

    var firstAppLogin = false
    var savedPointResponse: SavedPointResponse = SavedPointResponse()

    /**
     * saveTargetType
     * @see io.temco.guhada.data.model.coupon.CouponConsumption.ConsumptionTargetType
     */
    class SavedPointResponse {
        var savedPoint = 0
        var dueSavedPoint = 0
        var totalFreePoint = 0
        var totalPaidPoint = 0
        var message: String? = ""
        var saveTargetType: String? = ""
    }
}