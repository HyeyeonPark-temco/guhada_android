package io.temco.guhada.data.model

/**
 * JWT 토큰 정보 클래스
 * @author Hyeyeon Park
 */
class Token {
    var accessToken: String? = null
    var refreshToken: String? = null
    var expiresIn: Long = 0
}