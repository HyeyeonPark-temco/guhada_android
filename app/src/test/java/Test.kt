import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class Test {

    @Test
    fun test() {
        System.out.println(DateTime(1571025200000).toString("yyyy.MM.dd hh:mm"))
        System.out.println(DateTime(1571025200000).toString("yyyy.MM.dd HH:mm"))
    }
}