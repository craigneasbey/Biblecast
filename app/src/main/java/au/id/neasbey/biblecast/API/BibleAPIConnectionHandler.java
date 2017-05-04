package au.id.neasbey.biblecast.API;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.UIUtils;
import au.id.neasbey.biblecast.util.URLWrapper;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Handles the HTTP connection to the Bible API
 */
public abstract class BibleAPIConnectionHandler {

    private static final String TAG = BibleAPIConnectionHandler.class.getSimpleName();

    private HttpURLConnection httpUrlConnection;

    private String username;

    private String password;

    protected HttpURLConnection getHttpUrlConnection() {
        return httpUrlConnection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Validates URL and opens connection to the BibleAPI
     *
     * @throws BibleSearchAPIException
     */
    public void connect(URLWrapper urlWrapper) throws BibleSearchAPIException {

        authenticate();

        try {
            // Send HTTP Get request
            httpUrlConnection = urlWrapper.openConnection();
        } catch (IOException io) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_error_connection) + io.getMessage());
        }
    }

    /**
     * By default, no authentication is required. Override this method set authentication.
     *
     * @throws BibleSearchAPIException
     */
    protected void authenticate() throws BibleSearchAPIException {
    };

    /**
     *
     * @return
     * @throws BibleSearchAPIException
     */
    public BibleAPIResponse getResponse() throws BibleSearchAPIException {
        BibleAPIResponse bibleAPIResponse = new BibleAPIResponse();

        getResponseStatus(bibleAPIResponse);
        getResponseData(bibleAPIResponse);

        return bibleAPIResponse;
    }

    /**
     * Retrieves the response status from the HTTP connection
     * @param bibleAPIResponse Collects response status
     * @throws BibleSearchAPIException
     */
    protected void getResponseStatus(BibleAPIResponse bibleAPIResponse) throws BibleSearchAPIException {

        try {
            bibleAPIResponse.setResponseCode(getHttpUrlConnection().getResponseCode());
            bibleAPIResponse.setResponseMessage(getHttpUrlConnection().getResponseMessage());
        } catch (IOException ioe) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_error_response) + ioe.getMessage());
        }

        Log.d(TAG, "Response code: " + bibleAPIResponse.getResponseCode());
        Log.d(TAG, "Response message: " + bibleAPIResponse.getResponseMessage());
    }

    /**
     * Get HTTP response data string
     *
     * @param bibleAPIResponse Response object
     * @throws BibleSearchAPIException
     */
    protected void getResponseData(BibleAPIResponse bibleAPIResponse) throws BibleSearchAPIException {

        BufferedReader reader = null;
        StringBuilder responseText = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(getHttpUrlConnection().getInputStream()));
            boolean newLine = false;
            String line;

            while ((line = reader.readLine()) != null) {
                if(newLine) {
                    responseText.append("\n");
                } else {
                    newLine = true;
                }

                responseText.append(line);
            }
        } catch (IOException ioe) {
            throw new BibleSearchAPIException(ioe.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
                Log.e(TAG, UIUtils.getContext().getString(R.string.api_error_close) + ex.getMessage());
            }
        }

        bibleAPIResponse.setResponseData(responseText.toString());
        Log.d(TAG, "Response data: " + bibleAPIResponse.getResponseData());
    }
}
