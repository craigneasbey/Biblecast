package au.id.neasbey.biblecast.API;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import au.id.neasbey.biblecast.API.BiblesOrg.BibleAPIBiblesOrg;
import au.id.neasbey.biblecast.API.BiblesOrg.BibleAPIConnectionHandlerBiblesOrg;
import au.id.neasbey.biblecast.API.BiblesOrg.BibleAPIResponseParserBiblesOrg;
import au.id.neasbey.biblecast.BibleSearch;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.GUIHelper.SearchSuggestionProvider;
import au.id.neasbey.biblecast.GUIHelper.SuggestionAsyncQueryHandler;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 1/10/15.
 *
 * Connects to the Bible REST JSON web service, performs a search query, updates the activity with the response JSON
 */
public class BibleAPITask extends AsyncTask<String, String, String> {

    private static final String TAG = BibleAPITask.class.getSimpleName();

    private BibleSearch bibleSearch;

    private BibleAPI bibleAPI;

    private BibleAPIQueryType queryType;

    public BibleAPITask(BibleSearch bibleSearch) {
        this.bibleSearch = bibleSearch;
    }

    protected void onPreExecute() {
        setupBibleAPI();

        bibleSearch.showProgress();
    }

    /**
     * Setup Bibles.org REST web service interface
     */
    protected void setupBibleAPI() {
        // Specifies Bibles.org Bible API
        BibleAPIConnectionHandler bibleAPIConnectionHandler = new BibleAPIConnectionHandlerBiblesOrg();
        BibleAPIResponseParser bibleAPIResponseParser = new BibleAPIResponseParserBiblesOrg();

        // Configure Bible API
        bibleAPI = new BibleAPIBiblesOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);
        bibleAPI.setUsername(bibleSearch.getText(R.string.api_username).toString());
        bibleAPI.setPassword(bibleSearch.getText(R.string.api_password).toString());
    }

    @Override
    protected String doInBackground(String... params) {
        return queryBibleAPI(params);
    }

    /**
     * Queries the Bible API
     *
     * @param params query parameters
     * @return Result response, if populated, an error occurred
     */
    private String queryBibleAPI(String... params) {

        queryType = BibleAPIQueryType.valueOf(params[0]);
        bibleAPI.setQueryType(queryType);

        switch(queryType) {
            case SEARCH:
                setQueryParameters(params[1], params[2], params[3]);
                break;
            case VERSION:
                bibleAPI.setURL(params[1]);
                bibleAPI.setLanguage(params[2]);
                break;
            case BOOK:
                bibleAPI.setURL(params[1]);
                break;
            default:
                setQueryParameters(params[1], params[2], params[3]);
        }

        return bibleAPI.query();
    }

    private void setQueryParameters(String url, String query, String version) {
        bibleAPI.setURL(url);
        bibleAPI.setQuery(query);
        bibleAPI.setVersions(version);
    }

    protected void onPostExecute(String result) {

        bibleSearch.closeDialog();

        if (isResultSuccessful(result)) {
            switch(queryType) {
                case SEARCH:
                    handleSearchResults();
                    break;
                case VERSION:
                    handleVersionResults();
                    break;
                case BOOK:
                    handleBookResults();
                    break;
                default:
                    handleSearchResults();
            }
        }
    }

    /**
     * Checks result response test and results to see if the query was successful
     * @param result Response test
     * @return {@code Boolean.TRUE} if results exist, otherwise {@code Boolean.FALSE}
     */
    private boolean isResultSuccessful(String result) {

        if (!TextUtils.isEmpty(result)) {
            // If result test is populated, an error occurred, then display the error
            Log.e(TAG, "Request Failed: " + result);

            UIUtils.displayError(bibleSearch.getActivity(), R.string.api_failed, bibleSearch.getText(R.string.ok).toString(), result);
        } else if (TextUtils.isEmpty(result) && bibleAPI.hasResults()) {
            // If result test is empty and there are results, update the results list displayed
            Log.d(TAG, "Request Successful");

            return true;
        } else {
            // If result test is empty and there are no results, then display the error
            Log.e(TAG, "No results");

            UIUtils.displayError(bibleSearch.getActivity(), R.string.api_request_complete, bibleSearch.getText(R.string.ok).toString(), bibleSearch.getText(R.string.api_no_results).toString());
        }

        return false;
    }

    private void handleSearchResults() {

        bibleSearch.setResultsExist(true);
        bibleAPI.updateResultList(bibleSearch.getResults());
        bibleSearch.updateResultView();
    }

    private void handleVersionResults() {

        bibleAPI.updateResultList(bibleSearch.getVersions());
        bibleSearch.updateVersionView();
    }

    private void handleBookResults() {

        bibleAPI.updateResultList(bibleSearch.getBooks());

        // update search suggestions
        SuggestionAsyncQueryHandler suggestionAsyncQueryHandler = new SuggestionAsyncQueryHandler(bibleSearch.getContentResolver());
        suggestionAsyncQueryHandler.startQuery(0, bibleSearch.getBooks(), SearchSuggestionProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    protected void onCancelled() {
        bibleSearch.closeDialog();
    }
}