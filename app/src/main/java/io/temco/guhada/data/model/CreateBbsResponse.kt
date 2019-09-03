package io.temco.guhada.data.model

import java.io.Serializable

class CreateBbsResponse : Serializable{

    var brandId : Long? = null
    var brandName = ""
    var categoryFilterId: Long? = null
    var categoryId = 0L
    var contents = ""
    var dealId : Long? = null
    var dealName = ""
    var delete = false
    var imageList : ArrayList<ImageResponse> = arrayListOf()
    var imageUrl = ""
    var title = ""
    var use = true

    override fun toString(): String {
        return "CreateBbsResponse(brandId=$brandId, brandName='$brandName', categoryFilterId=$categoryFilterId, categoryId=$categoryId, contents='$contents', dealId=$dealId, dealName='$dealName', delete=$delete, imageList=$imageList, imageUrl='$imageUrl', title='$title', use=$use)"
    }
}



class CreateBbsTempResponse : Serializable{

    var brandId : Long? = null
    var brandName = ""
    var contents = ""
    var dealId : Long? = null
    var dealName = ""
    var imageList : ArrayList<ImageResponse> = arrayListOf()
    var title = ""

    override fun toString(): String {
        return "CreateBbsTempResponse(brandId=$brandId, brandName='$brandName', contents='$contents', dealId=$dealId, dealName='$dealName', imageList=$imageList, title='$title')"
    }

}


