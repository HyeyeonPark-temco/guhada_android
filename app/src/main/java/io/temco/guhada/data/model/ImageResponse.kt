package io.temco.guhada.data.model

class ImageResponse {
    var url = ""
    var fileName = ""
    var fileContentType = ""
    var fileSize = 0L
    var imageWidth = 0
    var imageHeight = 0
    var index = 0

    override fun toString(): String {
        return "ImageResponse(url='$url', fileName='$fileName', fileContentType='$fileContentType', fileSize=$fileSize, imageWidth=$imageWidth, imageHeight=$imageHeight, index=$index)"
    }

}