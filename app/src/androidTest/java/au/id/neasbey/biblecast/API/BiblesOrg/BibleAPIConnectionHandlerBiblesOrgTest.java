package au.id.neasbey.biblecast.API.BiblesOrg;

import android.text.Spanned;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIQueryType;
import au.id.neasbey.biblecast.API.BibleAPIResponse;
import au.id.neasbey.biblecast.util.HttpUtils;
import au.id.neasbey.biblecast.util.URLWrapper;

import static org.mockito.Mockito.when;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Tests the Bibles.org connection handler
 */
public class BibleAPIConnectionHandlerBiblesOrgTest extends TestCase {

    private BibleAPIConnectionHandler objectUnderTest;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        objectUnderTest = new BibleAPIConnectionHandlerBiblesOrg();
    }

    public void testConnectAuthentication() throws Exception {

        final List<Spanned> expectedList = BibleAPIResponseParserBiblesOrgTest.createSearchList(BibleAPIResponseParserBiblesOrgTest.passageType);
        final String responseJSON = BibleAPIResponseParserBiblesOrgTest.createSearchJSON(BibleAPIResponseParserBiblesOrgTest.passageType, true);

        InputStream stream = new ByteArrayInputStream(responseJSON.getBytes(HttpUtils.UTF_8));

        URLWrapper urlWrapper = Mockito.mock(URLWrapper.class);
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);

        when(mockHttpURLConnection.getResponseCode()).thenReturn(BibleAPIResponse.RESPONSE_CODE_OK);
        when(mockHttpURLConnection.getResponseMessage()).thenReturn(BibleAPIResponse.RESPONSE_MESSAGE_OK);
        when(mockHttpURLConnection.getInputStream()).thenReturn(stream);
        when(urlWrapper.openConnection()).thenReturn(mockHttpURLConnection);

        String apiUsername = "authentication";
        String apiPassword = "test";

        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.connect(urlWrapper);

        InetAddress rAddr = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        int rPort = 80;
        String rProtocol = null;
        String rPrompt = null;
        String rScheme = null;

        PasswordAuthentication passwordAuthentication = Authenticator.requestPasswordAuthentication(rAddr, rPort, rProtocol, rPrompt, rScheme);

        assertEquals(apiUsername, passwordAuthentication.getUserName());
    }

    public void testGetResponse() throws Exception {

        final List<Spanned> expectedList = BibleAPIResponseParserBiblesOrgTest.createSearchList(BibleAPIResponseParserBiblesOrgTest.passageType);
        final String expectedResponseData = BibleAPIResponseParserBiblesOrgTest.createSearchJSON(BibleAPIResponseParserBiblesOrgTest.passageType, true);

        InputStream stream = new ByteArrayInputStream(expectedResponseData.getBytes(HttpUtils.UTF_8));

        URLWrapper urlWrapper = Mockito.mock(URLWrapper.class);
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);

        when(mockHttpURLConnection.getResponseCode()).thenReturn(BibleAPIResponse.RESPONSE_CODE_OK);
        when(mockHttpURLConnection.getResponseMessage()).thenReturn(BibleAPIResponse.RESPONSE_MESSAGE_OK);
        when(mockHttpURLConnection.getInputStream()).thenReturn(stream);
        when(urlWrapper.openConnection()).thenReturn(mockHttpURLConnection);

        String apiUsername = "authentication";
        String apiPassword = "test";

        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.connect(urlWrapper);

        BibleAPIResponse bibleAPIResponse = objectUnderTest.getResponse();

        assertTrue(BibleAPIResponse.RESPONSE_CODE_OK == bibleAPIResponse.getResponseCode());
        assertEquals(BibleAPIResponse.RESPONSE_MESSAGE_OK, bibleAPIResponse.getResponseMessage());
        assertEquals(expectedResponseData, bibleAPIResponse.getResponseData());
    }
}
