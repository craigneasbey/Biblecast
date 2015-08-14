package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.Spanned;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.util.StringUtils;

import static org.mockito.Mockito.when;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Tests the Bible.org response handler
 */
public class BibleAPIResponseHandlerBibleOrgTest extends TestCase {

    private String createResponse(List<Spanned> testList) {
        return BibleAPIResponseParserBibleOrgTest.createJSONAndListHTML(testList, BibleAPIResponseParserBibleOrgTest.passageType, true);
    }

    public void testGetResultList() throws Exception {
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
        BibleAPIConnectionHandler bibleAPIConnectionHandler = Mockito.mock(BibleAPIConnectionHandlerBibleOrg.class);
        BibleAPIResponseParser bibleAPIResponseParser = new BibleAPIResponseParserBibleOrg();

        List<Spanned> expectedList = new LinkedList<>();
        String responseString = createResponse(expectedList);
        InputStream stream = new ByteArrayInputStream(responseString.getBytes(StringUtils.UTF_8));

        when(bibleAPIConnectionHandler.getHttpUrlConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(200);
        when(mockHttpURLConnection.getResponseMessage()).thenReturn("OK");
        when(mockHttpURLConnection.getInputStream()).thenReturn(stream);

        BibleAPIResponseHandler objectUnderTest = new BibleAPIResponseHandlerBibleOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);
        List<Spanned> actualList = objectUnderTest.returnResultList();

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }
}
