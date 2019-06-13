package io.temco.guhada.data.model

class Review {
    var satisfaction: Satisfaction = Satisfaction()
    var totalReviewsCount: Int = 0
    var averageReviewsRating: Double = 0.0

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