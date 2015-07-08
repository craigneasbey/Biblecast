package au.id.neasbey.biblecast.util;

import junit.framework.TestCase;

/**
 * Created by craigneasbey on 30/06/15.
 */
public class UtilTest extends TestCase {

    public void testBase64() {
        String apiToken = "989879879jhljhlsjdfalsjbfebb2l3ib2lj3:";

        String basicAuth = "Basic " + new String(android.util.Base64.encode(apiToken.getBytes(), android.util.Base64.NO_WRAP));

        assertEquals("Basic OTg5ODc5ODc5amhsamhsc2pkZmFsc2piZmViYjJsM2liMmxqMzo=", basicAuth);
    }
}
