package au.id.neasbey.biblecast.API;

import android.text.Spanned;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by craigneasbey on 11/08/15.
 */
public abstract class BibleAPIResponseHandler {

    private static final String TAG = BibleAPIResponseHandler.class.getSimpleName();

    private HttpURLConnection httpUrlConnection;

    private BibleAPIResponseParser bibleAPIResponseParser;

    private int responseCode;

    private String responseMessage;

    private List<Spanned> resultList;

    public BibleAPIResponseHandler(BibleAPIConnectionHandler bibleAPIConnectionHandler, BibleAPIResponseParser bibleAPIResponseParser) {
        this.httpUrlConnection = bibleAPIConnectionHandler.getHttpUrlConnection();
        this.bibleAPIResponseParser = bibleAPIResponseParser;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    protected List<Spanned> getResultList() {
        return resultList;
    }

    protected HttpURLConnection getHttpUrlConnection() {
        return httpUrlConnection;
    }

    public List<Spanned> returnResultList() throws BibleSearchAPIException {

        resultList = new LinkedList<>();

        getServerResponse();
        checkServerResponse();

        bibleAPIResponseParser.parseResponseToList(getServerResponseData(), getResultList());

        return getResultList();
    }

    private void getServerResponse() throws BibleSearchAPIException {

        try {
            responseCode = getHttpUrlConnection().getResponseCode();
            responseMessage = getHttpUrlConnection().getResponseMessage();

            Log.d(TAG, "Response code: " + responseCode);
            Log.d(TAG, "Response message: " + responseMessage);
        } catch (IOException ioe) {
            throw new BibleSearchAPIException("API response error: " + ioe.getMessage());
        }
    }

    protected abstract void checkServerResponse() throws BibleSearchAPIException;

    /**
     * Get server response string
     */
    protected String getServerResponseData() throws BibleSearchAPIException {

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

            Log.d(TAG, "Response: " + responseText.toString());
        } catch (IOException ioe) {
            throw new BibleSearchAPIException(ioe.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
                Log.e(TAG, "Failed to close input reader: " + ex.getMessage());
            }
        }

        return responseText.toString();
    }
}
