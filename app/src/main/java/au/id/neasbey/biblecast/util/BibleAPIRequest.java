package au.id.neasbey.biblecast.util;

import android.text.Spanned;
import android.text.TextUtils;
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
 *
 * Bible API request handling
 */
public class BibleAPIRequest {

    private static final String TAG = BibleAPIRequest.class.getSimpleName();

    private String requestUrl = "";

    private String apiAuth = "";

    private final List<Spanned> resultList = new LinkedList<>();

    public String createRequestUrl(String apiUrl, String apiAuth, String apiQuery, String bibleVersions) {
        boolean missing = false;

        if(TextUtils.isEmpty(apiUrl))
        {
            Log.e(TAG, "No API URL specified");
            missing = true;
        }

        if(TextUtils.isEmpty(apiAuth))
        {
            Log.e(TAG, "No API authentication specified");
            missing = true;
        } else {
            this.apiAuth = apiAuth;
        }

        if (TextUtils.isEmpty(apiQuery)) {
            Log.e(TAG, "No API query specified");
            missing = true;
        }

        if (TextUtils.isEmpty(bibleVersions)) {
            Log.e(TAG, "No API bible versions specified");
            missing = true;
        }

        if(!missing) {
            try {
                // Set request address
                requestUrl += apiUrl;

                // Set request parameters
                requestUrl += "?query=" + URLEncoder.encode(apiQuery, "UTF-8");
                requestUrl += "&version=" + URLEncoder.encode(bibleVersions, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "URL encoding Error: " + e.getMessage());
            }
        }

        return requestUrl;
    }

    public String performRequest() {
        Log.d(TAG, "Request: " + requestUrl);

        String resultToDisplay = "";

        if(isRequestValid()) {
            try {
                HttpURLConnection urlConnection = createConnection(requestUrl, apiAuth);

                checkServerResponseOk(urlConnection);
                handleResponse(urlConnection);

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

    private HttpURLConnection createConnection(String requestUrl, String apiAuth) throws IOException {
        // Send HTTP Get request
        URL url = new URL(requestUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        // Set API authentication
        urlConnection.addRequestProperty("Authorization", generateHttpAuthentication(apiAuth.getBytes()));

        return urlConnection;
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
        StringBuilder responseText = new StringBuilder();

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

    private void checkServerResponseOk(HttpURLConnection urlConnection) throws Exception {
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
    }

    public static String generateHttpAuthentication(byte[] bytes) {
        return "Basic " + new String(android.util.Base64.encode(bytes, android.util.Base64.NO_WRAP));
    }
}
