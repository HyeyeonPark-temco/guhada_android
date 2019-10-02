package io.temco.guhada.data.model.community

import io.temco.guhada.common.util.CustomLog
import java.io.Serializable

/**
 *
 * 커뮤니티 소분류 정보
 */
class CommunityCategorySub : Serializable {

    var id = 0
    var communityId = 0
    var priority = 0
    var use = false
    var name = ""
    var imageUrl : String ? = null
    var desc : String ? = null
    var type = ""
    var requiredProductSearch = false
    var visibleProductSearch = false
    var createdAt = ""
    var createdBy = ""
    var updatedAt = ""
    var updatedBy = ""
    var filterList : Any ? = null

    // 소분류 커뮤니티 카테고리별 필터 정보
    var categoryFilterList : ArrayList<CommunityCategoryfilter> = arrayListOf()

    override fun toString(): String {
        if(CustomLog.flag)return "CommunityCategorySub(communityId=$communityId, priority=$priority, use=$use, name='$name', imageUrl=$imageUrl, desc=$desc, type='$type', requiredProductSearch=$requiredProductSearch, visibleProductSearch=$visibleProductSearch, createdAt='$createdAt', createdBy='$createdBy', updatedAt='$updatedAt', updatedBy='$updatedBy', filterList=$filterList, categoryFilterList=$categoryFilterList)"
        else return ""
    }


}