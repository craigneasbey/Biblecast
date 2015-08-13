package au.id.neasbey.biblecast.API.BibleOrg;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mockito;

import java.net.HttpURLConnection;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.util.URLWrapper;
import au.id.neasbey.biblecast.util.StringUtils;

import static org.mockito.Mockito.when;

/**
 * Created by craigneasbey on 11/08/15.
 */
public class BibleAPIConnectionHandlerBibleOrgTest extends TestCase {

    private BibleAPIConnectionHandler objectUnderTest;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        objectUnderTest = new BibleAPIConnectionHandlerBibleOrg();
    }

    public void testConnectAuthentication() throws Exception {
        URLWrapper urlWrapper = Mockito.mock(URLWrapper.class);
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);

        when(urlWrapper.openConnection()).thenReturn(mockHttpURLConnection);

        String authStr = "Authorization";
        String apiAuth = "authentication:test";

        objectUnderTest.setAuth(apiAuth);
        objectUnderTest.connect(urlWrapper);

        Mockito.verify(mockHttpURLConnection).addRequestProperty(Matchers.eq(authStr), Matchers.eq(StringUtils.generateHttpAuthentication(apiAuth.getBytes())));
    }
}
