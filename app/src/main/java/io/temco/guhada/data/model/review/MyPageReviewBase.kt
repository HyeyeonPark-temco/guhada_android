package io.temco.guhada.data.model.review

import java.io.Serializable

enum class MyPageReviewType {AvailableReview, MyReview, None}

open abstract class MyPageReviewBase : Serializable {
    abstract val type : MyPageReviewType

    var isMoreList = false
}