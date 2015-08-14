package au.id.neasbey.biblecast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIBibleOrg;
import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIConnectionHandlerBibleOrg;
import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIResponseHandlerBibleOrg;
import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIResponseParserBibleOrg;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.util.UIUtils;
import au.id.neasbey.biblecast.util.URLWrapper;

/**
 * Created by craigneasbey on 30/06/15.
 *
 * Activity that allows the user to enter bible search term and return results from a web service
 */
public class BibleSearch extends AppCompatActivity {

    private static final String TAG = BibleSearch.class.getSimpleName();
    private String apiURL;
    private String apiAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UIUtils.setContext(this);
        setContentView(R.layout.activity_bible_search);

        apiURL = getText(R.string.api_url).toString();
        apiAuth = getText(R.string.api_auth).toString();
        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Get the intent, verify the action is a search and get the query
     *
     * @param intent New intent search action
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new BibleAPITask().execute(query, apiURL, apiAuth);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bible_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Activity getActivity() {
        return this;
    }

    /**
     * Connects to the Bible REST JSON web service, performs a search query, updates the activity with the response JSON
     */
    private class BibleAPITask extends AsyncTask<String, String, String> {

        private final ProgressDialog progressDialog = new ProgressDialog(BibleSearch.this);
        private final String bibleVersions = "eng-KJV";
        private final List<Spanned> resultList = new LinkedList<>();
        private BibleAPI bibleAPI;
        private ArrayAdapter<Spanned> resultsAdapter;

        protected void onPreExecute() {
            // Start Progress Dialog (Message)
            progressDialog.setMessage(getText(R.string.ui_wait));
            progressDialog.show();

            // Link the listView to the array adapter so that the results can be displayed
            resultsAdapter = new ArrayAdapter<>(BibleSearch.this, android.R.layout.simple_list_item_1, resultList);

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(resultsAdapter);
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

            bibleAPI = new BibleAPIBibleOrg();
            bibleAPI.setQuery(params[0]);
            bibleAPI.setURL(params[1]);
            bibleAPI.setAuth(params[2]);
            bibleAPI.setVersions(bibleVersions);

            try {
                bibleAPI.performRequest(createBibleAPIResponseHandler());
            } catch (BibleSearchAPIException bsae) {
                return bsae.getMessage();
            }

            return bibleAPI.getResult();
        }

        /**
         * Create a connection to the Bible API and pass it to the Bible API Response Handler.
         * Provide the response parser to assist in the conversion to display result
         *
         * @return
         * @throws BibleSearchAPIException
         */
        private BibleAPIResponseHandler createBibleAPIResponseHandler() throws BibleSearchAPIException {

            URLWrapper urlWrapper = new URLWrapper(bibleAPI.getRequestURL());
            BibleAPIConnectionHandler bibleAPIConnectionHandler = new BibleAPIConnectionHandlerBibleOrg();
            bibleAPIConnectionHandler.setAuth(bibleAPI.getAuth());
            bibleAPIConnectionHandler.connect(urlWrapper);

            BibleAPIResponseParser bibleAPIResponseParser = new BibleAPIResponseParserBibleOrg();

            return new BibleAPIResponseHandlerBibleOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);
        }

        protected void onPostExecute(String result) {

            // Close progress dialog
            progressDialog.dismiss();

            if (isResultSuccessful(result)) {
                // Notify the list adapter the data has changed
                resultsAdapter.notifyDataSetChanged();

                // Hide the entry keyboard
                SearchView searchView = (SearchView) findViewById(R.id.searchView);
                searchView.clearFocus();
            }
        }

        /**
         * Checks result response test and results to see if the query was successful
         * @param result
         * @return
         */
        private boolean isResultSuccessful(String result) {

            if (!TextUtils.isEmpty(result)) {
                // If result test is populated, an error occurred, then display the error
                Log.e(TAG, "Request Failed: " + result);

                UIUtils.displayError(getActivity(), R.string.api_failed, getText(R.string.ok).toString(), result);
            } else if (TextUtils.isEmpty(result) && bibleAPI.hasResults()) {
                // If result test is empty and there are results, update the results list displayed
                Log.d(TAG, "Request Successful");

                bibleAPI.updateResultList(resultList);

                return true;
            } else {
                // If result test is empty and there are no results, then display the error
                Log.e(TAG, "No results");

                UIUtils.displayError(getActivity(),
                        R.string.api_request_complete,
                        getText(R.string.ok).toString(),
                        getText(R.string.api_no_results).toString());
            }

            return false;
        }
    }
}
