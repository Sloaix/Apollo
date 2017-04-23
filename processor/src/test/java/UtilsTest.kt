import com.lsxiao.apllo.processor.Utils
import junit.framework.TestCase
import org.junit.Test
import java.util.*

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-23 11:24
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */


class UtilsTest : TestCase() {
    @Test
    fun testArraySplitBy() {
        assertEquals(""""a","b","c"""", Utils.split(Arrays.asList(*arrayOf("a", "b", "c")), ","))

        assertEquals(""""a","b"""", Utils.split(Arrays.asList(*arrayOf("a", "b")), ","))

        assertEquals(""""a"""", Utils.split(Arrays.asList(*arrayOf("a")), ","))
    }
}
