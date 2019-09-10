package io.temco.guhada.data.model

class UserClaimSellerResponse {
    var contents = ""
    var imageUrls : ArrayList<String> = arrayListOf()
    var title = ""
    var orderProdGroupId = 0L
    var type = ""
    var sellerId = 0L
    override fun toString(): String {
        return "UserClaimSellerResponse(contents='$contents', imageUrls=$imageUrls, title='$title', orderProdGroupId=$orderProdGroupId, type='$type')"
    }

}