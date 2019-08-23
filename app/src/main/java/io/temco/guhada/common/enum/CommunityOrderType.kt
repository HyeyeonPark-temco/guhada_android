package io.temco.guhada.common.enum

/**
 * 커뮤니티 게시판 정렬 enum class
 * @author Hyeyeon Park
 * @since 2019.08.21
 */
enum class CommunityOrderType(val type: String, val label: String) {
    DATE_ASC("DATE_ASC", ""),
    DATE_DESC("DATE_DESC", "최신순"),
    VIEW_ASC("VIEW_ASC", ""),
    VIEW_DESC("VIEW_DESC", "조회순"),
    LIKE_ASC("LIKE_ASC", ""),
    LIKE_DESC("LIKE_DESC", "좋아요순"),
    COMMENT_ASC("COMMENT_ASC", ""),
    COMMENT_DESC("COMMENT_DESC", "댓글순")
}
