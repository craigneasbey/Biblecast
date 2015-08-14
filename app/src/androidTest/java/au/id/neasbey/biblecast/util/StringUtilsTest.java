package au.id.neasbey.biblecast.util;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Test the string utilities
 */
public class StringUtilsTest extends TestCase {

    public void testUrlEncodeUTF8Map() throws Exception {
        String expected = "query=test%26query&version=test%3Cversion";

        Map<String, String> parameters = new HashMap<>();
        parameters.put("query", "test&query");
        parameters.put("version", "test<version");

        String actual = StringUtils.urlEncodeUTF8(parameters);

        assertEquals(expected, actual);
    }

    public void testUrlEncodeUTF8() throws Exception {
        String expected = "test%26query";

        String actual = StringUtils.urlEncodeUTF8("test&query");

        assertEquals(expected, actual);
    }

    public void testGenerateHttpAuthentication() throws Exception {
        String apiToken = "989879879jhljhlsjdfalsjbfebb2l3ib2lj3:";
        String expected = "Basic OTg5ODc5ODc5amhsamhsc2pkZmFsc2piZmViYjJsM2liMmxqMzo=";

        assertEquals(expected, StringUtils.generateHttpAuthentication(apiToken.getBytes()));
    }
}
