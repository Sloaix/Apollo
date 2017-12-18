import com.lsxiao.apllo.processor.Util
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


class ProcessUtilTest : TestCase() {
    @Test
    fun testSplit() {
        assertEquals(""""a","b","c"""", Util.split(Arrays.asList("a", "b", "c"), ","))

        assertEquals(""""a","b"""", Util.split(Arrays.asList("a", "b"), ","))

        assertEquals(""""a"""", Util.split(Arrays.asList("a"), ","))

        assertEquals("", Util.split(Arrays.asList(), ","))
    }
}
