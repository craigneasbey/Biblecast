package au.id.neasbey.biblecast.util;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Test the string utilities
 */
public class HttpUtilsTest extends TestCase {

    public void testUrlEncodeUTF8Map() throws Exception {
        final String expected = "query=test%26query&version=test%3Cversion";

        Map<String, String> parameters = new HashMap<>();
        parameters.put("query", "test&query");
        parameters.put("version", "test<version");

        String actual = HttpUtils.urlEncodeUTF8(parameters);

        assertEquals(expected, actual);
    }

    public void testUrlEncodeUTF8() throws Exception {
        final String expected = "test%26query";

        String actual = HttpUtils.urlEncodeUTF8("test&query");

        assertEquals(expected, actual);
    }
}
