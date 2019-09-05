package io.temco.guhada.data.model

class UserClaimGuhadaResponse {
        var content = ""
        var imageUrls : ArrayList<String> = arrayListOf()
        var title = ""
        var typeCode = ""

    override fun toString(): String {
        return "UserClaimGuhadaResponse(content='$content', imageUrls=$imageUrls, title='$title', typeCode='$typeCode')"
    }
}