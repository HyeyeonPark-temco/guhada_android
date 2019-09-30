package io.temco.guhada.data.model.claim

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.base.BasePageModel
import java.io.Serializable

class MyPageClaimSeller : BasePageModel(), Serializable {

    @SerializedName("content")
    var content : ArrayList<MyPageClaimSellerContent> = arrayListOf()

}

class MyPageClaimSellerContent : Serializable {

    var id = 0
    var contents = ""
    var productName = ""
    var nickname = ""
    var productImageUrl = ""
    var brandName = ""
    var season : String? = null
    var createdAt : Long? = null
    var repliedAt : Long? = null
    var reply : Any? = null
    var type = ""

    /*var id = 0
    var orderProdGroupId = 0
    var sellerId = 0
    var type = MyPageClaimSellerType()
    var title = ""
    var contents = ""
    var userId = 0
    var reply : Any? = null
    var replier : Any? = null
    var replied = false
    var replyUpdated = false
    var createdAt = 0L
    var repliedAt : Any? = null*/


    var totalPages : Int = -1
    var pageNumber : Int = -1


}

/*
class MyPageClaimSellerType : Serializable {
    var name = ""
    var description = ""
}*/
