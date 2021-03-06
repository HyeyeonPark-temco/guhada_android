package io.temco.guhada.data.model.community

import io.temco.guhada.common.util.CustomLog
import java.io.Serializable

/**
 * /community/all or /community
 *
 * 커뮤니티 대분류 정보
 *
 */
class CommunityCategory : Serializable {
    var id = 0
    var priority = 0
    var use = false
    var name = ""
    var imageUr = ""
    var desc = ""
    var createdAt = ""
    var createdBy = ""
    var updatedAt = ""
    var updatedBy = ""

    override fun toString(): String {
        if(CustomLog.flag)return "CommunityCategory(id=$id, priority=$priority, use=$use, name='$name', imageUr='$imageUr', desc='$desc', createdAt='$createdAt', createdBy='$createdBy', updatedAt='$updatedAt', updatedBy='$updatedBy')"
        else return ""
    }


}