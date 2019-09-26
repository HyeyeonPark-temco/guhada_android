import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class Test {

    @Test
    fun test() {
        val t = android.util.Patterns.PHONE.matcher("01076652371").matches()
        val t2 =android.util.Patterns.PHONE.matcher("0107665237").matches()
        Assert.assertNotNull(t)
        Assert.assertNotNull(t2)
    }
}