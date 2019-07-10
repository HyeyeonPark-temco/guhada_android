package io.temco.guhada.data.model

import android.annotation.SuppressLint
import android.os.Build
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class ClaimResponse {
    var content: MutableList<Claim> = ArrayList()
    var pageable: Paging = Paging()
    var sort: Sort = Sort()

    var totalPages = 0
    var totalElements = 0
    var numberOfElements = 0
    var size = 0
    var number = 0

    @SerializedName("last")
    var last = false
    var first = false

    @SerializedName("empty")
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
        var replyAt : ArrayList<Int> = arrayListOf()
//            get() = convertDateTimeFormat(field)

        var createdAt: ArrayList<Int> = arrayListOf()
        //  get() = convertDateTimeFormat(field)

        var updatedAt:  ArrayList<Int> = arrayListOf()
//            get() = convertDateTimeFormat(field)

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

}