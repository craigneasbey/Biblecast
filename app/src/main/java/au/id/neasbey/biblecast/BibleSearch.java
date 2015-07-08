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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.util.BibleAPIRequest;
import au.id.neasbey.biblecast.util.BibleAPIResponseParser;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 30/06/15.
 */
public class BibleSearch extends AppCompatActivity {

    private static final String TAG = BibleSearch.class.getSimpleName();
    private final String apiURL = "https://bibles.org/v2/search.js";
    private String apiToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_search);

        apiToken = getText(R.string.api_token).toString();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Get the intent, verify the action and get the query
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new BibleAPI().execute(apiURL, apiToken, query);
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
    private class BibleAPI extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog = new ProgressDialog(BibleSearch.this);

        private ArrayAdapter<Spanned> resultsAdapter;
        private String bibleVersions = "eng-KJV";
        private List<Spanned> resultList = new LinkedList<>();
        private List<Spanned> returnList;

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

            String resultToDisplay;
            String apiUrl = params[0]; // API URL
            String apiAuth = params[1] + ":"; // API token in key/value pair
            String apiQuery = params[2]; // API query

            BibleAPIRequest bibleRequest = new BibleAPIRequest();
            bibleRequest.createRequestUrl(apiUrl, apiAuth, apiQuery, bibleVersions);
            resultToDisplay = bibleRequest.performRequest();
            returnList = bibleRequest.getResultsList();

            return resultToDisplay;
        }

        protected void onPostExecute(String result) {

            // Close progress dialog
            progressDialog.dismiss();

            if(isSuccessfulResult(result)) {
                // Notify the list adapter the data has changed
                resultsAdapter.notifyDataSetChanged();
            }
        }

        private boolean isSuccessfulResult(String result) {

            // Handle any errors
            if (result != null && !result.isEmpty()) {
                Log.e(TAG, "Request Failed: " + result);

                UIUtils.displayError(getActivity(), R.string.api_failed, getText(R.string.ok).toString(), result);
            } else if (result != null && returnList != null && !returnList.isEmpty()) {
                Log.d(TAG, "Request Successful");

                updateResultList();

                return true;
            } else {
                Log.e(TAG, "No results");

                UIUtils.displayError(getActivity(),
                        R.string.api_request_complete,
                        getText(R.string.ok).toString(),
                        getText(R.string.api_no_results).toString());
            }

            return false;
        }

        /**
         * Update the displayed list from the list returned from the web service
         */
        private void updateResultList() {
            for(Spanned html : returnList) {
                resultList.add(html);
            }

            returnList = null;
        }
    }
}
