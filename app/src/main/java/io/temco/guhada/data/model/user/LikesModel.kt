package io.temco.guhada.data.model.user

/**
 * http://dev.user.guhada.com/swagger-ui.html#/USER-LIKE/saveUsingPOST_2
 * 회원 좋아요
 */
enum class LikesTargetType { BBS , COMMENT }

class LikesModel{
    var target = ""
    var targetId = 0
}