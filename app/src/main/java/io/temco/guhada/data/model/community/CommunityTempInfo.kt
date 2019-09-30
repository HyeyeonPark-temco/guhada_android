package io.temco.guhada.data.model.community

import io.temco.guhada.data.model.CreateBbsResponse
import io.temco.guhada.data.model.Image
import io.temco.guhada.data.model.ImageResponse
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
    var tempImageList : ArrayList<ImageResponse>? = null

    fun getCreateBbsResponse() : CreateBbsResponse{
        var data = CreateBbsResponse()
        data.brandId = brandId
        data.brandName = brandName ?: ""
        data.dealId = dealId
        data.dealName = dealName ?: ""
        data.title = title ?: ""
        data.contents = contents ?: ""
        if(!tempImageList.isNullOrEmpty()) data.imageList.addAll(tempImageList!!)
        return data
    }

}