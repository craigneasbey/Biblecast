package au.id.neasbey.biblecast.util;

import junit.framework.TestCase;

/**
 * Created by craigneasbey on 9/07/15.
 *
 * Test the bible request utility
 */
public class BibleAPIRequestTest extends TestCase {

    private BibleAPIRequest objectOnTest;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        objectOnTest = new BibleAPIRequest();
    }

    public void testCreateValidRequestUrl() throws Exception {
        String expected = "https://bibles.org/v2/search.js?query=John+3%3A16&version=eng-KJV";

        String apiUrl ="https://bibles.org/v2/search.js";
        String apiAuth = "authentication:test";
        String apiQuery = "John 3:16";
        String bibleVersions = "eng-KJV";

        String actual = objectOnTest.createRequestUrl(apiUrl, apiAuth, apiQuery, bibleVersions);

        assertEquals(expected, actual);
        assertTrue(objectOnTest.isRequestValid());
    }

    public void testCreateInValidRequestUrl() throws Exception {
        String expected = "";

        String apiUrl ="https://bibles.org/v2/search.js";
        String apiQuery = "";
        String apiAuth = "authentication:test";
        String bibleVersions = "eng-KJV";

        String actual = objectOnTest.createRequestUrl(apiUrl, apiAuth, apiQuery, bibleVersions);

        assertEquals(expected, actual);
        assertFalse(objectOnTest.isRequestValid());
    }

    public void testPerformRequest() throws Exception {
        // TODO Mock HttpURLConnection
    }

    public void testGenerateHttpAuthentication() throws Exception {
        String apiToken = "989879879jhljhlsjdfalsjbfebb2l3ib2lj3:";
        String expected = "Basic OTg5ODc5ODc5amhsamhsc2pkZmFsc2piZmViYjJsM2liMmxqMzo=";

        assertEquals(expected, BibleAPIRequest.generateHttpAuthentication(apiToken.getBytes()));
    }
}