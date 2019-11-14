package io.temco.guhada.common.enum

/**
 * SNS 타입 enum
 * @author Hyeyeon Park
 * @since 2019.11.14
 */
enum class SnsLoginType(val type : String) {
    GOOGLE("GOOGLE"), NAVER("NAVER"),
    KAKAO("KAKAO"),  FACEBOOK("FACEBOOK")
}