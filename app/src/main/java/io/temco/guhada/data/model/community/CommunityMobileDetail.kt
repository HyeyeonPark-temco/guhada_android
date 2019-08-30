package io.temco.guhada.data.model.community

import io.temco.guhada.data.model.ImageResponse

enum class CommunityMobileDetailType {IMAGE, TEXT, NONE}

open class CommunityMobileDetail(var type : CommunityMobileDetailType)

class CommunityMobileDetailImage : CommunityMobileDetail(CommunityMobileDetailType.IMAGE) {
    var image = ImageResponse()
}

class CommunityMobileDetailText : CommunityMobileDetail(CommunityMobileDetailType.TEXT){
    var text = ""
}