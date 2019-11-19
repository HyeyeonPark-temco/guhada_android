import android.util.Log
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
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


    @Test
    fun op() {
        print(execute(1, Operation.Increment))
        print(execute(1, Operation.Add(2)))

        val ui = Ui() + UiOp.Show + UiOp.Hide
//        run(View(BaseApplication.getInstance().applicationContext), ui)


        print(String.format("반품 사유로 인해 반품배송비 %,2d원을 %s으로 구매자가 부담합니다.", 1000, "<<환불 방법>>"))

        val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJreXVuZ3N1LmxlZUB0ZW1jby5pbyIsInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTc0MDkwNzI4LCJ1c2VySWQiOjEwLCJhdXRob3JpdGllcyI6WyJCQlNfQ09NTUVOVF9SRUdJU1RFUiIsIlNXSVRDSF9UT19TRUxMRVIiLCJCQlNfQ09NTUVOVF9NT0RJRllfT1JfREVMRVRFIiwiQkJTX0JPT0tNQVJLIiwiQkJTX1BPU1RfSU5RVUlSWSIsIkJCU19QT1NUX0xJS0VfQU5EX0JPT0tNQVJLIiwiUkVQT1JUIiwiQkJTX1BPU1RfTU9ESUZZX0FORF9ERUxFVEUiLCJQUk9EVUNUX0lOUVVJUlkiLCJST0xFX1VTRVIiLCJQUk9EVUNUX0xJS0VfQU5EX0JPT0tNQVJLIiwiQkJTX0lOUVVJUlkiLCJCQlNfUE9TVF9XUklURSIsIlBST0RVQ1RfU0VBUkNIIiwiUk9MRV9CQlMiXSwianRpIjoiODI2ZmQ0NjgtOTc2ZS00NGE4LTg3ZjktMDRhNzZkMWQxMWMxIiwiY2xpZW50X2lkIjoiZ3VoYWRhIn0.X3F9TCKKS8gDnyzUhTRLwsPZC8h2bX3tlrp9_bXtnuU"
        print(token.split("Bearer ")[1])
    }


    class Ui(val uiOps: List<Any> = emptyList()) {
        operator fun plus(uiOp: UiOp) = Ui(uiOps + uiOp)
    }

    sealed class UiOp {
        object Show : UiOp()
        object Hide : UiOp()
    }

    sealed class Operation {
        class Add(val value: Int) : Operation()
        class Multiply(val value: Int) : Operation()
        object Increment : Operation()
    }

    fun execute(x: Int, op: Operation) = when (op) {
        is Operation.Add -> x + op.value
        is Operation.Multiply -> x * op.value
        is Operation.Increment -> x + 1
    }

    fun uiExecute(view: View, op: UiOp) = when (op) {
        UiOp.Show -> view.visibility = View.VISIBLE
        UiOp.Hide -> view.visibility = View.GONE
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