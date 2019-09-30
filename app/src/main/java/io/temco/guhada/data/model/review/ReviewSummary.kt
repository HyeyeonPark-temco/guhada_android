package io.temco.guhada.data.model.review

import com.google.gson.annotations.SerializedName

/**
 * 상품 리뷰 정보 클래스
 * 만족도 그래프, 총 리뷰 수, 평점 포함
 * @author Hyeyeon Park
 */
class ReviewSummary {
    @SerializedName("satisfactionSummary ")
    var satisfaction: Satisfaction = Satisfaction()
    var totalReviewsCount: Int = 0
    var averageReviewsRating: Float = 0.0f



    class Satisfaction {
        var sizes: List<SatisfactionContent> = ArrayList()
        var colors: List<SatisfactionContent> = ArrayList()
        var lengths: List<SatisfactionContent> = ArrayList()
    }
    
    class SatisfactionContent {
        var name: String = ""
        var description: String = ""
        var count: Int = 0


    }

}