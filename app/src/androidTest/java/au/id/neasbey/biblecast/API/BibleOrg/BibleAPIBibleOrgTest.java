package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.Spanned;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.util.URLWrapper;

import static org.mockito.Mockito.when;

/**
 * Created by craigneasbey on 11/08/15.
 */
public class BibleAPIBibleOrgTest extends TestCase {

    private BibleAPI objectUnderTest;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        objectUnderTest = new BibleAPIBibleOrg();
    }

    private List<Spanned> createResultList() {
        List<Spanned> testList = new LinkedList<>();
        BibleAPIResponseParserBibleOrgTest.createJSON(testList, BibleAPIResponseParserBibleOrgTest.passageType, true);

        return testList;
    }

    public void testGetRequestParameters() throws Exception {
        String expected = "?query=John+3%3A16&version=eng-KJV";

        String apiQuery = "John 3:16";
        String bibleVersions = "eng-KJV";

        objectUnderTest.setQuery(apiQuery);
        objectUnderTest.setVersions(bibleVersions);

        String actual = objectUnderTest.getRequestParameters();

        assertEquals(expected, actual);
    }


    public void testGetRequestURL() throws Exception {
        String expected = "https://bibles.org/v2/search.js?query=John+3%3A16&version=eng-KJV";

        String apiQuery = "John 3:16";
        String apiUrl ="https://bibles.org/v2/search.js";
        String apiAuth = "authentication:test";
        String bibleVersions = "eng-KJV";

        objectUnderTest.setQuery(apiQuery);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setAuth(apiAuth);
        objectUnderTest.setVersions(bibleVersions);

        String actual = objectUnderTest.getRequestURL();

        assertEquals(expected, actual);
    }

    public void testPerformRequest() throws Exception {
        List<Spanned> expectedList = createResultList();
        List<Spanned> actualList = new LinkedList<>();

        BibleAPIResponseHandler bibleAPIResponseHandler = Mockito.mock(BibleAPIResponseHandlerBibleOrg.class);

        when(bibleAPIResponseHandler.returnResultList()).thenReturn(expectedList);

        objectUnderTest.performRequest(bibleAPIResponseHandler);

        assertEquals(null, objectUnderTest.getResult());
        assertTrue(objectUnderTest.hasResults());

        objectUnderTest.updateResultList(actualList);

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }
}
