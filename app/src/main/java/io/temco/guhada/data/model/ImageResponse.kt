package io.temco.guhada.data.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ImageResponse : Serializable{
    var url = ""
    var fileName = ""
    var fileContentType = ""
    var fileSize = 0L
    var imageWidth = 0
    var imageHeight = 0

    @Expose
    var index : Int? = 0

}