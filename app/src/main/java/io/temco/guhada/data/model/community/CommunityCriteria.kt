package io.temco.guhada.data.model.community

/**
 * 커뮤니티 게시판 검색 모델
 * @author Hyeyeon Park
 * @since 2019.08.21
 */
class CommunityCriteria {
    var categoryId = 0L     // 게시판 카테고리 아이디
    var filterId = 0L       // 게시판 필터 아이디
    var deleted = false     // 게시판 delete 필터
    var inUse = true        // 게시판 use 필터
    var query = ""          // 게시판 검색 쿼리
    var searchType = SearchType.TITLE_CONTENTS.type     // 게시판 검색 타입

    enum class SearchType(val type: String) {
        CONTENTS("CONTENTS"),
        TITLE("TITLE"),
        TITLE_CONTENTS("TITLE_CONTENTS"),
        COMMENTS("COMMENTS"),
        BBS_WRITER("BBS_WRITER"),
        COMMENT_WITTER("COMMENT_WITTER ")
    }
}
