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
    fun getToday(): Calendar = Calendar.getInstance(TimeZone.getDefault())


    @JvmStatic
    fun getTodayToString(type: Type.DateFormat): String {
        val c = Calendar.getInstance(TimeZone.getDefault())
        return getCalendarToString(type, c)
    }


    // Calendar String 변환
    @SuppressLint("SimpleDateFormat")
    fun getCalendarToString(type: Type.DateFormat, mt: Long?): String {
        if (mt != null) {
            var c = Calendar.getInstance().apply { timeInMillis = mt }
            val f = SimpleDateFormat(Type.DateFormat.get(type))
            return f.format(c.time)
        }
        return ""
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
     * @param now 현재 TimeStamp (ms)
     * @param date 비교 대상 TimeStamp (ms)
     * @param isMs ms 단위인지 여부 (default = false)
     *
     * @author Hyeyeon Park
     * @since 2019.08.22
     */
    @JvmStatic
    fun getDateDiff(now: Long, date: Long, isMs: Boolean = false): String {
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val DAY_MS = HOUR_MS * 24
        val diffSeconds = if (isMs) (now - date) else (now - date) * 1000

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

    /**
     * 24시간 이내 작성된 글인지 체크
     *
     * @param now 현재 TimeStamp
     * @param date 비교 대상 TimeStamp
     * @param isMs ms 단위인지 여부 (default = false)
     *
     * @author Hyeyeon Park
     * @since 2019.09.20
     */
    @JvmStatic
    fun checkNewly(now: Long, date: Long, isMs: Boolean = false): Boolean {
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val DAY_MS = HOUR_MS * 24
        val diffSeconds = if (isMs) (now - date) else (now - date) * 1000
        return diffSeconds < DAY_MS
    }

    /**
     * convert timestamp
     * @param separator 년월일 구분자
     *
     * @author Hyeyeon Park
     * @since 2019.09.11
     */
    @JvmStatic
    fun convertTimestamp(timestamp: Long, separator: String): String = DateTime(timestamp).toString("yyyy${separator}MM${separator}dd")

    @JvmStatic
    fun convertDateTimestamp(timestamp: Long, separator: String): String = DateTime(timestamp).toString("yyyy${separator}MM${separator}dd HH:mm")

    @JvmStatic
    fun getNowDateDiffMinute(date: Long): Int {
        if (date == 0L) return 0
        val MINUTE_MS = 60 * 1000
        val today = Calendar.getInstance()
        val minute = (today.timeInMillis - date) / MINUTE_MS
        return minute.toInt()
    }


    @JvmStatic
    fun getNowDateDiffHour(date: Long): Int {
        if (date == 0L) return 0
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val today = Calendar.getInstance()
        val minute = (today.timeInMillis - date) / HOUR_MS
        return minute.toInt()
    }

    @JvmStatic
    fun getNowDateDiffDay(date: Long): Int {
        if (date == 0L) return 0
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val DAY_MS = HOUR_MS * 24
        val today = Calendar.getInstance()
        val minute = (today.timeInMillis - date) / DAY_MS
        return minute.toInt()
    }


    /**
     * TimeZone offset
     * 서버에서 내려오는 모든 timestamp는 utc
     * @author Hyeyeon Park
     */
    @JvmStatic
     fun getTimezoneOffsetMs(): Int {
        val cal = Calendar.getInstance()
        val timeZone = cal.timeZone
        val offset = timeZone.rawOffset + timeZone.dstSavings
        return offset
    }


}