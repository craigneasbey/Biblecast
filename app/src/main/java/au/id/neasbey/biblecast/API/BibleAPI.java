package au.id.neasbey.biblecast.API;

import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Search using the Bible API
 */
public abstract class BibleAPI {

    private static final String TAG = BibleAPI.class.getSimpleName();

    private String query;

    private String URL;

    private String auth;

    private String versions;

    private List<Spanned> returnedList;

    private String result;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public List<Spanned> getReturnedList() {
        return returnedList;
    }

    protected void setReturnedList(List<Spanned> returnedList) {
        this.returnedList = returnedList;
    }

    public String getResult() {
        return result;
    }

    protected void setResult(String result) {
        this.result = result;
    }

    /**
     * Performs the search(query) on specified in the URL
     *
     * @param bibleAPIResponseHandler Handler for response from the Bible API
     */
    public void performRequest(BibleAPIResponseHandler bibleAPIResponseHandler) {

        try {
            setReturnedList(bibleAPIResponseHandler.returnResultList());
        } catch (BibleSearchAPIException bsae) {
            setResult(bsae.getMessage());
        }
    }

    /**
     * Gets the URL for the Bible API request
     * Note: Changed protected to public class access for testing
     *
     * @return Complete URL with encoded parameters
     * @throws BibleSearchAPIException
     */
    public String getRequestURL() throws BibleSearchAPIException {

        if (TextUtils.isEmpty(getURL())) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_no_url));
        }

        StringBuilder requestText = new StringBuilder();

        requestText.append(getURL());
        requestText.append(getRequestParameters());

        Log.d(TAG, "Request: " + requestText.toString());

        return requestText.toString();
    }

    /**
     * Checks the API parameters, then encodes them
     *
     * @return Encoded parameters for the API URL
     * @throws BibleSearchAPIException
     */
    public abstract String getRequestParameters() throws BibleSearchAPIException;

    /**
     * Checks if results are returned
     * @return {@code Boolean.TRUE} if there are results, otherwise {@code Boolean.FALSE}
      */
    public boolean hasResults() {
        return returnedList != null && !returnedList.isEmpty();
    }

    /**
     * Update the displayed list from the list returned from the web service
     *
     * @param resultList Results from the Bible API search
     */
    public void updateResultList(List<Spanned> resultList) {
        resultList.clear();

        for (Spanned html : returnedList) {
            if(!TextUtils.isEmpty(html)) {
                resultList.add(html);
            }
        }

        // free memory
        setReturnedList(null);
    }
}
