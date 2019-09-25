package io.temco.guhada.data.model.review

import io.temco.guhada.data.model.product.ProductOption

/**
 * 상품 리뷰 정보 클래스
 * ReviewResponse > ReviewResponseContent > Review
 * @author Hyeyeon Park
 */
class ReviewResponseContent {
    var review: Review = Review()
    var order: Any? = null    // 확인 필요
    var reviewPhotos: MutableList<ReviewPhoto>? = ArrayList()
    var productOption: ProductOption = ProductOption()
    var userSize : UserSize?= UserSize()
    var likesUserIds: Array<Long>? = arrayOf()
    var reviewTexts: ReviewSatisfactionText = ReviewSatisfactionText()

    fun getLikeCount() = if (likesUserIds == null) "0" else likesUserIds?.size.toString()

    class ReviewSatisfactionText {
        var size = ""
        var color = ""
        var length = ""
    }

    class UserSize {
        var height = 0
        var weight = 0
        var shoe = 0
        var bottom = 0
        var top = ""
    }

    class ReviewPhoto{
        var id = 0L
        var userProductReviewId = 0L
        var reviewPhotoUrl: String = ""
        var photoOrder = 0
    }
}