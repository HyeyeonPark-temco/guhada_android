package io.temco.guhada.data.model

class ReviewResponseContent {
    var review: Review = Review()
    var order: Any? = null    // 확인 필요
    var photoUrls: MutableList<String> = ArrayList()
    var productOption: ProductOption = ProductOption()
    var userSize: String? = ""    // 확인 필요
    var likesUserIds: Array<Long>? = arrayOf()
    var reviewTexts: ReviewSatisfactionText = ReviewSatisfactionText()

    fun getLikeCount() = if (likesUserIds == null) "0" else likesUserIds?.size.toString()

    class ReviewSatisfactionText {
        var size = ""
        var color = ""
        var length = ""
    }
}