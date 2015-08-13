package au.id.neasbey.biblecast.API;

import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by craigneasbey on 11/08/15.
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

    public void performRequest(BibleAPIResponseHandler bibleAPIResponseHandler) {

        try {
            setReturnedList(bibleAPIResponseHandler.returnResultList());
        } catch (BibleSearchAPIException bsae) {
            setResult(bsae.getMessage());
        }
    }

    /**
     * Changed protected to public for testing
     * @return
     * @throws BibleSearchAPIException
     */
    public String getRequestURL() throws BibleSearchAPIException {

        if (TextUtils.isEmpty(getURL())) {
            throw new BibleSearchAPIException("No API URL specified");
        }

        StringBuilder requestText = new StringBuilder();

        requestText.append(getURL());
        requestText.append(getRequestParameters());

        Log.d(TAG, "Request: " + requestText.toString());

        return requestText.toString();
    }

    public abstract String getRequestParameters() throws BibleSearchAPIException;

    public boolean hasResults() {
        return returnedList != null && !returnedList.isEmpty();
    }

    /**
     * Update the displayed list from the list returned from the web service
     */
    public void updateResultList(List<Spanned> resultList) {
        for (Spanned html : returnedList) {
            if(!TextUtils.isEmpty(html)) {
                resultList.add(html);
            }
        }

        // free memory
        setReturnedList(null);
    }
}
