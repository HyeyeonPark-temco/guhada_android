package io.temco.guhada.data.model

import android.annotation.SuppressLint
import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class ClaimResponse {
    var content: MutableList<Claim> = ArrayList()
    var pageable: Paging = Paging()
    var sort: Sort = Sort()

    var totalPages = 0
    var totalElements = 0
    var numberOfElements = 0
    var size = 0
    var number = 0

    var last = false
    var first = false
    var empty = false

    class Claim {
        var id: Int = 0
        var productId: Int = 0
        var inquirer: Int = 0
        var replier: Int = 0

        var replyUpdated: Boolean = false
        var enable: Boolean = false
        var private: Boolean = false

        var status: String = ""
        var inquiry: String = ""
        var nickname: String = ""
        var reply: String? = null
        var replyAt: String = ""
            get() = convertDateTimeFormat(field)

        var createdAt: String = ""
            get() = convertDateTimeFormat(field)

        var updatedAt: String = ""
            get() = convertDateTimeFormat(field)

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

        private fun plusZero(number: Int): String = if (number < 10) {
            "0$number"
        } else {
            number.toString()
        }
    }

    class Paging {
        var sort: Sort = Sort()
        var pageSize = 0
        var pageNumber = 0
        var offset = 0

        var paged: Boolean = false
        var unpaged: Boolean = false
    }

    class Sort {
        var sorted: Boolean = false
        var unsorted: Boolean = false
        var empty: Boolean = false
    }
}