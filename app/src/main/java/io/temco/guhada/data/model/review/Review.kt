package io.temco.guhada.data.model.review

import android.annotation.SuppressLint
import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime

/**
 * 상품 리뷰 정보 클래스
 * ReviewResponse > ReviewResponseContent > Review
 * @author Hyeyeon Park
 */
class Review {
    // ID
    var id: Long = 0
    var userId: Long = 0
    var orderProductGroupId: Long = 0
    var productId: Long = 0

    // SATISFACTION
    var sizeSatisfaction = ""
    var colorSatisfaction = ""
    var lengthSatisfaction = ""

    // REVIEW
    var photoCount = 0
    var likeCount = 0
    var bookmarkCount = 0
    var userNickname = ""
    var productRating = ""
    var profileImageUrl = ""
    var textReview = ""
    var createdAt = ""
        get() = convertDateTimeFormat(field)

    fun getRating(): Float = when (productRating) {
        "HALF" -> 0.5f
        "ONE" -> 1.0f
        "ONE_HALF" -> 1.5f
        "TWO" -> 2.0f
        "TWO_HALF" -> 2.5f
        "THREE" -> 3.0f
        "THREE_HALF" -> 3.5f
        "FOUR" -> 4.0f
        "FOUR_HALF" -> 4.5f
        "FIVE" -> 5.0f
        else -> 0.0f
    }

    /// 추후 Util로 분리 예정
    @SuppressLint("SimpleDateFormat")
    private fun convertDateTimeFormat(str: String?): String {
        return if (str != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.parse(str).let {
                    "${it.year}.${plusZero(it.month.value)}.${plusZero(it.dayOfMonth)} ${plusZero(it.hour)}:${plusZero(it.minute)}"
                }
            } else {
                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(str)
                val hour = plusZero(date.hours)
                val month = plusZero(date.month)

                val year = plusZero(SimpleDateFormat("yy").format(date).toInt())
                val day = plusZero(SimpleDateFormat("dd").format(date).toInt())
                val minute = plusZero(SimpleDateFormat("mm").format(date).toInt())

                "$year.$month.$day $hour:$minute"
            }
        } else {
            ""
        }
    }

    /// 추후 Util로 분리 예정
    private fun plusZero(number: Int): String = if (number < 10) {
        "0$number"
    } else {
        number.toString()
    }

}

