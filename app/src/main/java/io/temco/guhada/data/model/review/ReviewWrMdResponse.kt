package io.temco.guhada.data.model.review

import com.google.gson.Gson

/**
 * @author park jungho
 *
 * 리뷰 작성/수정 response
 *
 */
class ReviewWrMdResponse {

    var colorSatisfaction = ""
    var lengthSatisfaction  = ""
    var orderProductGroupId  = 0
    var productRating  = ""
    var sellerId = 0L
    var sizeSatisfaction  = ""
    var productId  = 0L
    var reviewId  = 0L
    var textReview = ""
    var reviewPhotos : MutableList<ReviewPhoto> = mutableListOf()
    override fun toString(): String {
        return "ReviewWrMdResponse(colorSatisfaction='$colorSatisfaction', lengthSatisfaction='$lengthSatisfaction', orderProductGroupId=$orderProductGroupId, productRating='$productRating', sellerId=$sellerId, sizeSatisfaction='$sizeSatisfaction', productId=$productId, reviewId=$reviewId, textReview='$textReview', reviewPhotos=$reviewPhotos)"
    }


}

enum class ImageStatus {UNCHANGED, ADDED, UPDATED, DELETED}

class ReviewPhoto {
    var id = 0
    var imageStatus = ""
    var photoOrder = 0
    var reviewPhotoUrl = ""
    override fun toString(): String {
        return "ReviewPhoto(id=$id, imageStatus='$imageStatus', photoOrder=$photoOrder, reviewPhotoUrl='$reviewPhotoUrl')"
    }

}