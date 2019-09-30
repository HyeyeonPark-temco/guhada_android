package io.temco.guhada.data.model.community

import java.io.Serializable

enum class CommunityType {
    MAIN,POPULAR,NOTIFICATION,LIST,
}

class CommunityInfo : Serializable {

    constructor()
    constructor(type : CommunityType, communityCategoryName: String) {
        this.type = type
        this.communityCategoryName = communityCategoryName
    }

    var type : CommunityType = CommunityType.LIST

    // 대분류 이름 (메인,인기글,공지사항은 대분류 이름 없음)
    var communityName = ""

    // 소분류 이름
    var communityCategoryName = ""

    // (메인,인기글,공지사항은 일단 없음)
    var communityId = 0

    // (메인,인기글,공지사항은 일단 없음)
    var communityCategoryId = 0

    var communityCategory = CommunityCategory()

    var communityCategorySub = CommunityCategorySub()



}