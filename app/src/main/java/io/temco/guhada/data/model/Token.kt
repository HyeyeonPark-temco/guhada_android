package io.temco.guhada.data.model

/**
 * JWT 토큰 정보 클래스
 * @author Hyeyeon Park
 */
class Token {
    var accessToken: String? = null
    var refreshToken: String? = null
    var expiresIn: Long = 0L
    var firstAppLogin = false
    var savedPointResponse: SavedPointResponse = SavedPointResponse()

    class SavedPointResponse {
        var savedPoint = 0
        var dueSavedPoint = 0
        var totalFreePoint = 0
        var totalPaidPoint = 0
        var saveTargetType: String? = ""
        var message: String? = ""
    }
}