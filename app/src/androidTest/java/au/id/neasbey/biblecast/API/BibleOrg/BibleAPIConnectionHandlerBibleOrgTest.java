package au.id.neasbey.biblecast.API.BibleOrg;

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
import au.id.neasbey.biblecast.API.BibleAPIResponse;
import au.id.neasbey.biblecast.util.HttpUtils;
import au.id.neasbey.biblecast.util.URLWrapper;

import static org.mockito.Mockito.when;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Tests the Bible.org connection handler
 */
public class BibleAPIConnectionHandlerBibleOrgTest extends TestCase {

    private BibleAPIConnectionHandler objectUnderTest;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        objectUnderTest = new BibleAPIConnectionHandlerBibleOrg();
    }

    private String createResponse(List<Spanned> testList) {
        return BibleAPIResponseParserBibleOrgTest.createJSONAndListHTML(testList, BibleAPIResponseParserBibleOrgTest.passageType, true);
    }

    public void testConnectAuthentication() throws Exception {

        List<Spanned> expectedList = new LinkedList<>();
        String responseString = createResponse(expectedList);
        InputStream stream = new ByteArrayInputStream(responseString.getBytes(HttpUtils.UTF_8));

        URLWrapper urlWrapper = Mockito.mock(URLWrapper.class);
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);

        when(mockHttpURLConnection.getResponseCode()).thenReturn(BibleAPIResponse.responseCodeOk);
        when(mockHttpURLConnection.getResponseMessage()).thenReturn(BibleAPIResponse.responseMessageOk);
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

        List<Spanned> expectedList = new LinkedList<>();
        String expectedResponseData = createResponse(expectedList);
        InputStream stream = new ByteArrayInputStream(expectedResponseData.getBytes(HttpUtils.UTF_8));

        URLWrapper urlWrapper = Mockito.mock(URLWrapper.class);
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);

        when(mockHttpURLConnection.getResponseCode()).thenReturn(BibleAPIResponse.responseCodeOk);
        when(mockHttpURLConnection.getResponseMessage()).thenReturn(BibleAPIResponse.responseMessageOk);
        when(mockHttpURLConnection.getInputStream()).thenReturn(stream);
        when(urlWrapper.openConnection()).thenReturn(mockHttpURLConnection);

        String apiUsername = "authentication";
        String apiPassword = "test";

        objectUnderTest.setUsername(apiUsername);
        objectUnderTest.setPassword(apiPassword);
        objectUnderTest.connect(urlWrapper);

        BibleAPIResponse bibleAPIResponse = objectUnderTest.getResponse();

        assertTrue(BibleAPIResponse.responseCodeOk == bibleAPIResponse.getResponseCode());
        assertEquals(BibleAPIResponse.responseMessageOk, bibleAPIResponse.getResponseMessage());
        assertEquals(expectedResponseData, bibleAPIResponse.getResponseData());
    }
}
