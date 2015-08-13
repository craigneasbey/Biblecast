package au.id.neasbey.biblecast.API;

import java.io.IOException;
import java.net.HttpURLConnection;

import au.id.neasbey.biblecast.util.URLWrapper;

/**
 * Created by craigneasbey on 11/08/15.
 */
public abstract class BibleAPIConnectionHandler {

    private HttpURLConnection httpUrlConnection;

    private String auth;

    public HttpURLConnection getHttpUrlConnection() {
        return httpUrlConnection;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    /**
     * Validates URL and opens connection to the BibleAPI
     *
     * @param url A wrapper with URL
     * @throws BibleSearchAPIException
     */
    public void connect(URLWrapper url) throws BibleSearchAPIException {

        try {
            // Send HTTP Get request
            httpUrlConnection = url.openConnection();

            authenticate();
        } catch (IOException io) {
            throw new BibleSearchAPIException("API connection error: " + io.getMessage());
        }
    }

    /**
     * By default, no authentication is required
     *
     * @throws BibleSearchAPIException
     */
    protected void authenticate() throws BibleSearchAPIException {
    };
}
