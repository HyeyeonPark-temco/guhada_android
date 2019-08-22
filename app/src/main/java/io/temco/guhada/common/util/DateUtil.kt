package io.temco.guhada.common.util

import android.annotation.SuppressLint
import io.temco.guhada.common.Type
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * 날짜 관련 Util
 */
object DateUtil {
    // 오늘 일자
    fun getToday(): Calendar {
        return Calendar.getInstance(TimeZone.getDefault())
    }


    @JvmStatic
    fun getTodayToString(type: Type.DateFormat): String {
        val c = Calendar.getInstance(TimeZone.getDefault())
        return getCalendarToString(type, c)
    }

    // Calendar String 변환
    @SuppressLint("SimpleDateFormat")
    fun getCalendarToString(type: Type.DateFormat, c: Calendar): String {
        val f = SimpleDateFormat(Type.DateFormat.get(type))
        return f.format(c.time)
    }

    /**
     * 날짜 차이 표시
     * - 1분 미만: 조금 전
     * - 1시간 미만: n분 전
     * - 1일 미만: n시간 전
     * - 1일 초과(같은 해): MM월 dd일
     * - 1일 초과(다른 해): yyyy년 MM월 dd일
     *
     * @param now 현재 TimeStamp
     * @param date 비교 대상 TimeStamp
     *
     * @author Hyeyeon Park
     * @since 2019.08.22
     */
    @JvmStatic
    fun getDateDiff(now : Long, date : Long):String{
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val DAY_MS = HOUR_MS * 24
        val diffSeconds = (now - date) * 1000

        return when {
            diffSeconds < MINUTE_MS -> "조금 전"
            diffSeconds < HOUR_MS -> "${diffSeconds / MINUTE_MS}분 전"
            diffSeconds < DAY_MS -> "${diffSeconds / HOUR_MS}시간 전"
            else -> { // 24시간 이상 전
                val boardDate = DateTime(date)
                val boardYear = boardDate.year
                val currentYear = DateTime(now).year

                if (boardYear < currentYear)
                    boardDate.toString("yyyy년 MM월 dd일")
                else
                    boardDate.toString("MM월 dd일")
            }
        }
    }
}