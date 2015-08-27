package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.Spanned;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponse;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.util.URLWrapper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by craigneasbey on 11/08/15.
 *
 *Test the Bible.org BIBLE API
 */
public class BibleAPIBibleOrgTest extends TestCase {

    private BibleAPI objectUnderTest;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        BibleAPIConnectionHandler bibleAPIConnectionHandler = new BibleAPIConnectionHandlerBibleOrg();
        BibleAPIResponseParser bibleAPIResponseParser = new BibleAPIResponseParserBibleOrg();

        objectUnderTest = new BibleAPIBibleOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);
    }

    private String createResultJSON() {
        return BibleAPIResponseParserBibleOrgTest.createJSONAndListHTML(null, BibleAPIResponseParserBibleOrgTest.passageType, true);
    }

    private List<Spanned> createResultList() {
        List<Spanned> testList = new LinkedList<>();
        BibleAPIResponseParserBibleOrgTest.createJSONAndListHTML(testList, BibleAPIResponseParserBibleOrgTest.passageType, true);

        return testList;
    }

    public void testGetRequestParameters() throws Exception {
        final String expected = "?query=John+3%3A16&version=eng-KJV";

        final String apiQuery = "John 3:16";
        final String bibleVersions = "eng-KJV";

        objectUnderTest.setQuery(apiQuery);
        objectUnderTest.setVersions(bibleVersions);

        String actual = objectUnderTest.getRequestParameters();

        assertEquals(expected, actual);
    }


    public void testGetRequestURL() throws Exception {
        final String expected = "https://bibles.org/v2/search.js?query=John+3%3A16&version=eng-KJV";

        final String apiQuery = "John 3:16";
        final String apiUrl ="https://bibles.org/v2/search.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";
        final String bibleVersions = "eng-KJV";

        objectUnderTest.setQuery(apiQuery);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.setVersions(bibleVersions);

        String actual = objectUnderTest.getRequestURL();

        assertEquals(expected, actual);
    }

    public void testQuery() throws Exception {
        final List<Spanned> expectedList = createResultList();
        List<Spanned> actualList = new LinkedList<>();
        final String responseJSON = createResultJSON();

        BibleAPIResponse bibleAPIResponse = new BibleAPIResponse();
        bibleAPIResponse.setResponseCode(BibleAPIResponse.responseCodeOk);
        bibleAPIResponse.setResponseMessage(BibleAPIResponse.responseMessageOk);
        bibleAPIResponse.setResponseData(responseJSON);

        BibleAPIConnectionHandler bibleAPIConnectionHandler = Mockito.mock(BibleAPIConnectionHandlerBibleOrg.class);
        BibleAPIResponseParser bibleAPIResponseParser = Mockito.mock(BibleAPIResponseParserBibleOrg.class);

        Mockito.doNothing().when(bibleAPIConnectionHandler).connect(any(URLWrapper.class));
        when(bibleAPIConnectionHandler.getResponse()).thenReturn(bibleAPIResponse);

        Mockito.doNothing().when(bibleAPIResponseParser).parseResponseStatus(anyInt(), anyString());
        when(bibleAPIResponseParser.parseResponseDataToList(anyString())).thenReturn(expectedList);

        objectUnderTest = new BibleAPIBibleOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);

        final String apiQuery = "John 3:16";
        final String apiUrl ="https://bibles.org/v2/search.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";
        final String bibleVersions = "eng-KJV";

        objectUnderTest.setQuery(apiQuery);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.setVersions(bibleVersions);


        assertEquals(null, objectUnderTest.query());
        assertTrue(objectUnderTest.hasResults());

        objectUnderTest.updateResultList(actualList);

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }
}
