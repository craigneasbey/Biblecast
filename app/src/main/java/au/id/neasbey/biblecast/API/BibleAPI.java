package au.id.neasbey.biblecast.API;

import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import au.id.neasbey.biblecast.BibleVersion;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.UIUtils;
import au.id.neasbey.biblecast.util.URLWrapper;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Search using the Bible API
 */
public abstract class BibleAPI {

    private static final String TAG = BibleAPI.class.getSimpleName();

    private BibleAPIConnectionHandler bibleAPIConnectionHandler;

    private BibleAPIResponseParser bibleAPIResponseParser;

    private BibleAPIQueryType queryType;

    private String query;

    private String URL;

    private String username;

    private String password;

    private String versions;

    private String language;

    private List<?> returnedList;

    public BibleAPI(BibleAPIConnectionHandler bibleAPIConnectionHandler, BibleAPIResponseParser bibleAPIResponseParser) {
        this.bibleAPIConnectionHandler = bibleAPIConnectionHandler;
        this.bibleAPIResponseParser = bibleAPIResponseParser;
    }

    /**
     * Performs the Bible API query
     *
     * @return Error string, otherwise null
     */
    public String query() {

        try {
            // create a connection to the Bible API and perform the query
            URLWrapper urlWrapper = new URLWrapper(getRequestURL());
            getBibleAPIConnectionHandler().connect(urlWrapper);

            // get the Bible API response
            BibleAPIResponse bibleAPIResponse = getBibleAPIConnectionHandler().getResponse();

            // parse the Bible API response
            getBibleAPIResponseParser().parseResponseStatus(bibleAPIResponse.getResponseCode(), bibleAPIResponse.getResponseMessage());

            switch(queryType) {
                case SEARCH:
                    setReturnedList(getBibleAPIResponseParser().parseResponseDataToSpannedList(bibleAPIResponse.getResponseData()));
                    break;
                case VERSION:
                    setReturnedList(getBibleAPIResponseParser().parseResponseDataToVersionList(bibleAPIResponse.getResponseData()));
                    break;
                case BOOK:
                    setReturnedList(getBibleAPIResponseParser().parseResponseDataToStringList(bibleAPIResponse.getResponseData()));
                    break;
                default:
                    setReturnedList(getBibleAPIResponseParser().parseResponseDataToSpannedList(bibleAPIResponse.getResponseData()));
            }
        } catch (BibleSearchAPIException bsae) {
            return bsae.getMessage();
        }

        return null;
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
     *
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
    public void updateResultList(List<?> resultList) {
        resultList.clear();

        switch(getQueryType()) {
            case SEARCH:
                updateSearchResults((List<Spanned>)resultList);
                break;
            case VERSION:
                updateVersionResults((List<BibleVersion>)resultList);
                break;
            case BOOK:
                updateBookResults((List<String>)resultList);
                break;
            default:
                updateSearchResults((List<Spanned>)resultList);
        }

        // ensure memory is freed
        setReturnedList(null);
    }

    private void updateSearchResults(List<Spanned> resultList) {
        for (Spanned html : (List<Spanned>)returnedList) {
            if(!TextUtils.isEmpty(html)) {
                resultList.add(html);
            }
        }
    }

    private void updateVersionResults(List<BibleVersion> resultList) {
        for (BibleVersion version: (List<BibleVersion>)returnedList) {
                resultList.add(version);
        }
    }

    private void updateBookResults(List<String> resultList) {
        for (String book : (List<String>)returnedList) {
            if(!TextUtils.isEmpty(book)) {
                resultList.add(book);
            }
        }
    }

    public BibleAPIQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(BibleAPIQueryType queryType) {
        this.queryType = queryType;
    }

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

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    protected void setReturnedList(List<?> returnedList) {
        this.returnedList = returnedList;
    }

    protected BibleAPIConnectionHandler getBibleAPIConnectionHandler() {
        return bibleAPIConnectionHandler;
    }

    protected BibleAPIResponseParser getBibleAPIResponseParser() {
        return bibleAPIResponseParser;
    }
}
