package io.temco.guhada.data.model.claim

import io.temco.guhada.common.util.CommonUtil
import java.io.Serializable

/**
 * 상품 문의 클래스
 * @see ClaimResponse
 * @author Hyeyeon Park
 */
class Claim : Serializable {
    var id: Int = 0
    var productId: Int = 0
    var inquirer: Int = 0
    var replier: Int = 0

    var replyUpdated: Boolean = false
    var enable: Boolean = false
    var private: Boolean = false

    var status: String = ""
    var inquiry: String = ""
    var nickname: String = ""
    var reply: String? = null

    // DATE TIME
    var replyAt = 0L
    var createdAt = 0L
    var updatedAt = 0L

    fun getCreatedAtDate() =
        CommonUtil.convertTimeStampToDate(createdAt)
    fun getReplyAtAtDate() =
            CommonUtil.convertTimeStampToDate(replyAt)
}