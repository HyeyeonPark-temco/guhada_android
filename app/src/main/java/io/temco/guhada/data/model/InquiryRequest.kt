package io.temco.guhada.data.model

import com.google.gson.annotations.Expose

class InquiryRequest {
    @Expose
    var productId: Long = 0

    var content: String = ""
    var privateInquiry: Boolean = false

    inner class Inquiry {
        var inquiryId: Int? = null
        var content: String = ""
        var privateInquiry: Boolean = false
    }
}