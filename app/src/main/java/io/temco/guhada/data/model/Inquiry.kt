package io.temco.guhada.data.model

import java.io.Serializable

class Inquiry : Serializable{
    var productId: Long = 0
    var content: String = ""
    var privateInquiry: Boolean = false
    var inquiryId: Int? = null
}