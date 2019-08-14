package io.temco.guhada.data.model.review

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

    fun setParam() : HashMap<String, Any>{
        var map : HashMap<String, Any> = hashMapOf()
        map.put("colorSatisfaction",colorSatisfaction)
        map.put("lengthSatisfaction",lengthSatisfaction)
        map.put("orderProductGroupId",orderProductGroupId)
        map.put("productRating",productRating)
        map.put("sellerId",sellerId)
        map.put("productId",productId)
        map.put("sizeSatisfaction",sizeSatisfaction)
        map.put("textReview",textReview)

        return map
    }

    fun setParamModify() : HashMap<String, Any>{
        var map : HashMap<String, Any> = hashMapOf()
        map.put("colorSatisfaction",colorSatisfaction)
        map.put("lengthSatisfaction",lengthSatisfaction)
        map.put("orderProductGroupId",orderProductGroupId)
        map.put("productRating",productRating)
        map.put("sellerId",sellerId)
        map.put("productId",productId)
        map.put("reviewId",reviewId)
        map.put("sizeSatisfaction",sizeSatisfaction)
        map.put("textReview",textReview)

        return map
    }


}