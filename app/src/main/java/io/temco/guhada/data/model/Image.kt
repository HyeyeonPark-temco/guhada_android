package io.temco.guhada.data.model

import java.io.Serializable

/**
 * 이미지 파일 정보 클래스
 * @see Deal
 * @author Hyeyeon Park
 */
class Image : Serializable {
    var name = ""
    var url = ""
    var width = 0
    var height = 0
}

