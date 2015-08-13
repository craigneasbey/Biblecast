package au.id.neasbey.biblecast.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by craigneasbey on 13/08/15.
 *
 * This wrapper class allow mocking during testing
 */
public class URLWrapper {

    private String urlString;

    public URLWrapper(String url) {
        this.urlString = url;
    }

    public HttpURLConnection openConnection() throws IOException {
        return ((HttpURLConnection)new URL(urlString).openConnection());
    }
}
