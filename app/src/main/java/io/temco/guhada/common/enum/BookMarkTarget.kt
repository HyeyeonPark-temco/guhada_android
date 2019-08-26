package io.temco.guhada.common.enum

/**
 * 북마크 Target enum 클래스
 * @author Hyeyeon Park
 */
enum class BookMarkTarget(val target: String) {
    PRODUCT("PRODUCT"),
    DEAL("SELLER"),
    BBS("SELLER"),
    COMMENT("SELLER"),
    STORE("SELLER"),
    REVIEW("REVIEW"),
    SELLER("SELLER")
}