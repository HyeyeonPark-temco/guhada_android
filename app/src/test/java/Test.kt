import android.util.Log
import io.temco.guhada.common.util.DateUtil
import org.joda.time.DateTime
import org.joda.time.Hours
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.BigInteger
import java.util.*

@RunWith(JUnit4::class)
class Test {

    @Test
    fun test() {
//        print(DateTime(1571025200000).toString("yyyy.MM.dd hh:mm"))
//        print(DateTime(1571025200000).toString("yyyy.MM.dd HH:mm"))
//        print(getDateDiffDay(1571820037, 2599117))

        print("now: " + convertDateTimestamp(1571899720000, "."))
        print("discountStartAt: " + convertDateTimestamp(1571637128000, "."))
        print("remainedTimeForEnd: " + getRemainEndAt(687807000))
        print("end: " + convertDateTimestamp(1571899720000 + 687807000, "."))

        print("########################################################################################")

        print("now: " + convertDateTimestamp(1571899720000 + getTimezoneOffsetMs(), "."))
        print("discountStartAt: " + convertDateTimestamp(1571637128000 + getTimezoneOffsetMs(), "."))

        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60

        val remainedTimeForEnd = HOUR_MS * 10.toLong() + (MINUTE_MS * 30)
        print("remainedTimeForEnd: " + getRemainEndAt(remainedTimeForEnd))
        print("end: " + convertDateTimestamp((1571899720000 + getTimezoneOffsetMs() + remainedTimeForEnd), "."))

//        print(getTimezoneOffsetMs())
//        getTimezoneInfo()
    }


    @Test
    fun timeDealTest() {
        print(DateUtil.getTimezoneOffsetMs())
    }

    private fun print(text: Any) {
        if (text is String) System.out.println(text)
        else System.out.println(text.toString())

    }

    private fun getDateDiffDay(now: Long, date: Long): Int {
        if (date == 0L) return 0
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val DAY_MS = HOUR_MS * 24

        val minute = (now - date) / DAY_MS
        return minute.toInt()
    }

    private fun convertDateTimestamp(timestamp: Long, dateOperator: String): String {
        val dateTime = DateTime(timestamp)
        val hour = dateTime.hourOfDay

        return when {
            hour < 12 -> "${DateTime(timestamp).toString("MM${dateOperator}dd HH")}AM"
            hour == 12 -> "${DateTime(timestamp).toString("MM${dateOperator}dd HH")}PM"
            else -> "${DateTime(timestamp).toString("MM${dateOperator}dd")} ${hour - 12}PM"
        }
    }

    private fun getRemainEndAt(timestamp: Long): String {
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60
        val DAY_MS = HOUR_MS * 24

        val remainDay = timestamp / DAY_MS
        return if (remainDay > 1) "${remainDay}일 남음"
        else {
            val hour = timestamp / HOUR_MS
            val minute = (timestamp % HOUR_MS) / MINUTE_MS
            val second = (timestamp % HOUR_MS) % MINUTE_MS
            "$hour:$minute:$second"
        }
    }

    private fun getTimezoneOffsetMs(): Int {
        val cal = Calendar.getInstance()
        val timeZone = cal.timeZone
        val offset = timeZone.rawOffset + timeZone.dstSavings
        return offset
    }

    private fun getTimezoneInfo() {
        val MINUTE_MS = 60 * 1000
        val HOUR_MS = MINUTE_MS * 60

        val cal = Calendar.getInstance()
        val timeZone = cal.timeZone
        val offset = timeZone.rawOffset + timeZone.dstSavings
        val offsetHour = offset / HOUR_MS

        print("timeZone: ${timeZone.displayName}")
        print("timeZone offset: $offset")
        print("timeZone offset hour: $offsetHour")
    }
}