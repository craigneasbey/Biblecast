package au.id.neasbey.biblecast.util;

import android.text.Spanned;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by craigneasbey on 8/07/15.
 */
public class BibleAPIRequest {

    private static final String TAG = BibleAPIRequest.class.getSimpleName();

    private String requestUrl = "";

    private String apiAuth = "";

    private List<Spanned> resultList = new LinkedList<>();

    public void createRequestUrl(String apiUrl, String apiAuth, String apiQuery, String bibleVersions) {
        this.apiAuth = apiAuth;

        if (apiQuery != null && apiQuery.isEmpty()) {
            Log.e(TAG, "No query specified");
        } else {
            try {
                // Set request address
                requestUrl += apiUrl;

                // Set request parameters
                requestUrl += "?query=" + URLEncoder.encode(apiQuery, "UTF-8");
                requestUrl += "&version=" + URLEncoder.encode(bibleVersions, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                Log.i(TAG, "URL encoding Error: " + e.getMessage());
            }
        }
    }

    public String performRequest() {
        Log.d(TAG, "Request: " + requestUrl);

        String resultToDisplay = "";

        if(isRequestValid()) {
            try {
                HttpURLConnection urlConnection = createConnection(requestUrl, apiAuth);

                if (isServerResponseOk(urlConnection)) {
                    handleResponse(urlConnection);
                }

            } catch (Exception e) {
                resultToDisplay = e.getMessage();
            }
        } else {
            resultToDisplay = "Please enter a valid query";
        }

        return resultToDisplay;
    }

    public boolean isRequestValid() {
        return !requestUrl.isEmpty();
    }

    public List<Spanned> getResultsList() {
        return resultList;
    }

    private void handleResponse(HttpURLConnection urlConnection) throws Exception {
        String responseJSON = getServerResponseJSON(urlConnection);

        // Parse server response
        BibleAPIResponseParser bibleAPIResponseParser = new BibleAPIResponseParser();
        bibleAPIResponseParser.parseJSONToList(responseJSON, resultList);
    }

    /**
     * Get server response JSON
     */
    private String getServerResponseJSON(HttpURLConnection urlConnection) throws Exception {

        BufferedReader reader = null;
        StringBuffer responseText = new StringBuffer();

        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                responseText.append(line);
                responseText.append("\n");
            }

            Log.d(TAG, "Response: " + responseText.toString());
        } catch (IOException ioe) {
            throw new Exception(ioe.getMessage());
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

    private boolean isServerResponseOk(HttpURLConnection urlConnection) throws Exception {
        int responseCode = urlConnection.getResponseCode();
        String responseMessage = urlConnection.getResponseMessage();

        Log.d(TAG, "Response code: " + responseCode);
        Log.d(TAG, "Response message: " + responseMessage);

        if (responseCode == 401) {
            throw new Exception("Application API token is incorrect");
        }

        if (responseCode != 200) {
            throw new Exception(responseCode + " - " + responseMessage);
        }

        return true;
    }

    private HttpURLConnection createConnection(String requestUrl, String apiAuth) throws IOException {
        // Send HTTP Get request
        URL url = new URL(requestUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        // Set application API token
        String basicAuth = "Basic " + new String(android.util.Base64.encode(apiAuth.getBytes(), android.util.Base64.NO_WRAP));
        urlConnection.addRequestProperty("Authorization", basicAuth);

        return urlConnection;
    }
}
