package io.temco.guhada.data.model.community

import io.temco.guhada.data.model.CreateBbsResponse
import java.io.Serializable

class CommunityTempInfo : Serializable{
    var id = 0L
    var brandId : Long? = null
    var brandName : String? = null
    var dealId : Long? = null
    var dealName : String? = null
    var title : String? = null
    var contents : String? = null
    var createdTimestamp : Long? = 0L
    var createdBy : String? = null

    fun getCreateBbsResponse() : CreateBbsResponse{
        var data = CreateBbsResponse()
        data.brandId = brandId
        data.brandName = brandName ?: ""
        data.dealId = dealId
        data.dealName = dealName ?: ""
        data.title = title ?: ""
        return data
    }

    override fun toString(): String {
        return "CommunityTempInfo(id=$id, brandId=$brandId, brandName=$brandName, dealId=$dealId, dealName=$dealName, title=$title, contents=$contents, createdTimestamp=$createdTimestamp, createdBy=$createdBy)"
    }

}