package au.id.neasbey.biblecast.API.BiblesOrg;

import android.text.Spanned;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIQueryType;
import au.id.neasbey.biblecast.API.BibleAPIResponse;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.BuildConfig;
import au.id.neasbey.biblecast.model.BibleVersion;
import au.id.neasbey.biblecast.util.URLWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by craigneasbey on 11/08/15.
 *
 *Test the Bibles.org BIBLE API
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class BibleAPIBiblesOrgTest {

    private BibleAPI objectUnderTest;

    @BeforeClass
    public static void setupRobolectric() {
        ShadowLog.stream = System.out;
    }

    @Before
    public void setUp() throws Exception {
        BibleAPIConnectionHandler bibleAPIConnectionHandler = new BibleAPIConnectionHandlerBiblesOrg();
        BibleAPIResponseParser bibleAPIResponseParser = new BibleAPIResponseParserBiblesOrg();

        objectUnderTest = new BibleAPIBiblesOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);
    }

    @Test
    public void shouldSuccessfullyGetSearchRequestParameters() throws Exception {
        final String expected = "?query=John+3%3A16&version=eng-KJV";

        final String apiQuery = "John 3:16";
        final String bibleVersions = "eng-KJV";

        objectUnderTest.setQueryType(BibleAPIQueryType.SEARCH);
        objectUnderTest.setQuery(apiQuery);
        objectUnderTest.setVersions(bibleVersions);

        String actual = objectUnderTest.getRequestParameters();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSuccessfullyGetVersionRequestParameters() throws Exception {
        final String expected = "?language=eng";

        final String language = "eng";

        objectUnderTest.setQueryType(BibleAPIQueryType.VERSION);
        objectUnderTest.setLanguage(language);

        String actual = objectUnderTest.getRequestParameters();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSuccessfullyGetNoRequestParameters() throws Exception {
        final String expected = "";

        objectUnderTest.setQueryType(BibleAPIQueryType.BOOK);

        String actual = objectUnderTest.getRequestParameters();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSuccessfullyGetSearchRequestURL() throws Exception {
        final String expected = "https://bibles.org/v2/search.js?query=John+3%3A16&version=eng-KJV";

        final String apiQuery = "John 3:16";
        final String apiUrl ="https://bibles.org/v2/search.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";
        final String bibleVersions = "eng-KJV";

        objectUnderTest.setQueryType(BibleAPIQueryType.SEARCH);
        objectUnderTest.setQuery(apiQuery);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.setVersions(bibleVersions);

        String actual = objectUnderTest.getRequestURL();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSuccessfullyGetVersionRequestURL() throws Exception {
        final String expected = "https://bibles.org/v2/versions.js?language=eng-US";

        final String apiUrl ="https://bibles.org/v2/versions.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";
        final String apiLanguage = "eng-US";

        objectUnderTest.setQueryType(BibleAPIQueryType.VERSION);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.setLanguage(apiLanguage);

        String actual = objectUnderTest.getRequestURL();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSuccessfullyGetBookRequestURL() throws Exception {
        final String expected = "https://bibles.org/v2/versions/eng-KJV/books.js";

        final String apiUrl ="https://bibles.org/v2/versions/eng-KJV/books.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";

        objectUnderTest.setQueryType(BibleAPIQueryType.BOOK);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);

        String actual = objectUnderTest.getRequestURL();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSuccessfullyQueryForSearchText() throws Exception {
        List<Spanned> actualList = new LinkedList<>();

        final List<Spanned> expectedList = BibleAPIResponseParserBiblesOrgTest.createSearchList(BibleAPIResponseParserBiblesOrgTest.passageType);
        final String responseJSON = BibleAPIResponseParserBiblesOrgTest.createSearchJSON(BibleAPIResponseParserBiblesOrgTest.passageType, true);

        BibleAPIResponse bibleAPIResponse = new BibleAPIResponse();
        bibleAPIResponse.setResponseCode(BibleAPIResponse.RESPONSE_CODE_OK);
        bibleAPIResponse.setResponseMessage(BibleAPIResponse.RESPONSE_MESSAGE_OK);
        bibleAPIResponse.setResponseData(responseJSON);

        BibleAPIConnectionHandler bibleAPIConnectionHandler = Mockito.mock(BibleAPIConnectionHandlerBiblesOrg.class);
        BibleAPIResponseParser bibleAPIResponseParser = Mockito.mock(BibleAPIResponseParserBiblesOrg.class);

        Mockito.doNothing().when(bibleAPIConnectionHandler).connect(any(URLWrapper.class));
        when(bibleAPIConnectionHandler.getResponse()).thenReturn(bibleAPIResponse);

        Mockito.doNothing().when(bibleAPIResponseParser).parseResponseStatus(anyInt(), anyString());
        when(bibleAPIResponseParser.parseResponseDataToSpannedList(anyString())).thenReturn(expectedList);

        objectUnderTest = new BibleAPIBiblesOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);

        final String apiQuery = "John 3:16";
        final String apiUrl ="https://bibles.org/v2/search.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";
        final String bibleVersions = "eng-KJV";

        objectUnderTest.setQueryType(BibleAPIQueryType.SEARCH);
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

    @Test
    public void shouldSuccessfullyQueryForVersions() throws Exception {
        List<BibleVersion> actualList = new LinkedList<>();

        final List<BibleVersion> expectedList = BibleAPIResponseParserBiblesOrgTest.createVersionList();
        final String responseJSON = BibleAPIResponseParserBiblesOrgTest.createVersionJSON(true);

        BibleAPIResponse bibleAPIResponse = new BibleAPIResponse();
        bibleAPIResponse.setResponseCode(BibleAPIResponse.RESPONSE_CODE_OK);
        bibleAPIResponse.setResponseMessage(BibleAPIResponse.RESPONSE_MESSAGE_OK);
        bibleAPIResponse.setResponseData(responseJSON);

        BibleAPIConnectionHandler bibleAPIConnectionHandler = Mockito.mock(BibleAPIConnectionHandlerBiblesOrg.class);
        BibleAPIResponseParser bibleAPIResponseParser = Mockito.mock(BibleAPIResponseParserBiblesOrg.class);

        Mockito.doNothing().when(bibleAPIConnectionHandler).connect(any(URLWrapper.class));
        when(bibleAPIConnectionHandler.getResponse()).thenReturn(bibleAPIResponse);

        Mockito.doNothing().when(bibleAPIResponseParser).parseResponseStatus(anyInt(), anyString());
        when(bibleAPIResponseParser.parseResponseDataToVersionList(anyString())).thenReturn(expectedList);

        objectUnderTest = new BibleAPIBiblesOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);

        final String apiUrl ="https://bibles.org/v2/versions.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";
        final String apiLanguage = "eng-US";

        objectUnderTest.setQueryType(BibleAPIQueryType.VERSION);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.setLanguage(apiLanguage);

        assertEquals(null, objectUnderTest.query());
        assertTrue(objectUnderTest.hasResults());

        objectUnderTest.updateResultList(actualList);

        assertTrue(actualList.size() > 0);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    @Test
    public void shouldSuccessfullyQueryForBooks() throws Exception {
        List<String> actualList = new LinkedList<>();

        final List<String> expectedList = BibleAPIResponseParserBiblesOrgTest.createBookList();
        final String responseJSON = BibleAPIResponseParserBiblesOrgTest.createBookJSON(true);

        BibleAPIResponse bibleAPIResponse = new BibleAPIResponse();
        bibleAPIResponse.setResponseCode(BibleAPIResponse.RESPONSE_CODE_OK);
        bibleAPIResponse.setResponseMessage(BibleAPIResponse.RESPONSE_MESSAGE_OK);
        bibleAPIResponse.setResponseData(responseJSON);

        BibleAPIConnectionHandler bibleAPIConnectionHandler = Mockito.mock(BibleAPIConnectionHandlerBiblesOrg.class);
        BibleAPIResponseParser bibleAPIResponseParser = Mockito.mock(BibleAPIResponseParserBiblesOrg.class);

        Mockito.doNothing().when(bibleAPIConnectionHandler).connect(any(URLWrapper.class));
        when(bibleAPIConnectionHandler.getResponse()).thenReturn(bibleAPIResponse);

        Mockito.doNothing().when(bibleAPIResponseParser).parseResponseStatus(anyInt(), anyString());
        when(bibleAPIResponseParser.parseResponseDataToStringList(anyString())).thenReturn(expectedList);

        objectUnderTest = new BibleAPIBiblesOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);

        final String apiUrl ="https://bibles.org/v2/versions.js";
        final String apiUsername = "authentication";
        final String apiPassword = "test";

        objectUnderTest.setQueryType(BibleAPIQueryType.BOOK);
        objectUnderTest.setURL(apiUrl);
        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);

        assertEquals(null, objectUnderTest.query());
        assertTrue(objectUnderTest.hasResults());

        objectUnderTest.updateResultList(actualList);

        assertTrue(actualList.size() > 0);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }
}
