package au.id.neasbey.biblecast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIQueryType;
import au.id.neasbey.biblecast.API.BibleAPITask;
import au.id.neasbey.biblecast.GUIHelper.SearchOnSuggestionListener;
import au.id.neasbey.biblecast.GUIHelper.VersionOnItemSelectedListener;
import au.id.neasbey.biblecast.cast.BibleCast;
import au.id.neasbey.biblecast.cast.ScrollGestureListener;
import au.id.neasbey.biblecast.model.BibleVersion;
import au.id.neasbey.biblecast.util.HttpUtils;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 30/06/15.
 *
 * Activity that allows the user to enter bible search term and return results from a web service
 */
public class BibleSearch extends AppCompatActivity {

    private static final String TAG = BibleSearch.class.getSimpleName();

    private static final String LANGUAGE_ENG_US = "eng-US";

    private static final String DEFAULT_VERSION = "eng-NASB";

    private static final String STATE_RESULTS = "results";

    private BibleCast bibleCast;

    private boolean resultsExist;

    private String bibleVersion;

    private Spinner versionSpinner;

    private List<BibleVersion> versionList;

    private ArrayAdapter<BibleVersion> versionAdapter;

    private List<String> bookList;

    private List<Spanned> resultList;

    private ArrayAdapter<Spanned> resultsAdapter;

    private ListView resultView;

    private GestureOverlayView gestureView;

    private ImageView scrollImageView;

    private GestureDetectorCompat mDetector;

    private ProgressDialog progressDialog;

    public BibleSearch() {
        bibleVersion = DEFAULT_VERSION;
        versionList = new LinkedList<>();
        bookList = new LinkedList<>();
        resultList = new LinkedList<>();
        resultsExist = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // restore previous results
        if(savedInstanceState != null) {
            resultList = (List<Spanned>) savedInstanceState.getSerializable(STATE_RESULTS);
        }

        setContentView(R.layout.activity_bible_search);
        UIUtils.setContext(this);
        bibleCast = new BibleCast(this);
        progressDialog = new ProgressDialog(this);

        setupResultsView();
        setupGestureView();
        setupVersionSpinner();

        getBibleVersions();
        getBookSuggestions();
    }

    /**
     * Link the resultView to the resultList via an array adapter so that the results can be displayed
     */
    private void setupResultsView() {
        // TODO change TextView resource to suit paragraphs
        resultsAdapter = new ArrayAdapter<>(this, R.layout.result_list_item, resultList);
        resultView = (ListView) findViewById(R.id.resultView);
        resultView.setAdapter(resultsAdapter);
    }

    /**
     * Setup the gestureView to handle scroll and fling gestures when connected to cast
     */
    protected void setupGestureView() {
        gestureView = (GestureOverlayView) findViewById(R.id.gestureView);
        gestureView.setOnTouchListener(new ScrollOnTouchListener());
        gestureView.setGestureVisible(false);
        mDetector = new GestureDetectorCompat(this, new ScrollGestureListener(this));

        scrollImageView = (ImageView) findViewById(R.id.scrollImageView);
    }

    private void setupVersionSpinner() {
        // may need this: http://stackoverflow.com/questions/1625249/android-how-to-bind-spinner-to-custom-object-list
        versionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, versionList);
        versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        versionSpinner = (Spinner) findViewById(R.id.versionSpinner);
        versionSpinner.setAdapter(versionAdapter);
        versionSpinner.setOnItemSelectedListener(new VersionOnItemSelectedListener(this));
    }

    private void getBibleVersions() {
        new BibleAPITask(this).execute(BibleAPIQueryType.VERSION.name(), getText(R.string.api_versions_url).toString(), LANGUAGE_ENG_US);
    }

    private void getBookSuggestions() {
        new BibleAPITask(this).execute(BibleAPIQueryType.BOOK.name(), getText(R.string.api_books_url).toString());
    }

    /**
     * Receive new intent and call handle function
     *
     * @param intent The intent for application search
     */
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
            // Handle search
            String query = intent.getStringExtra(SearchManager.QUERY);

            if(!TextUtils.isEmpty(query)) {
                new BibleAPITask(this).execute(BibleAPIQueryType.SEARCH.name(), getText(R.string.api_search_url).toString(), query.toLowerCase(), bibleVersion);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        bibleCast.startMediaDiscovery();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");

        bibleCast.endMediaDiscovery();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        // Close cast connection
        bibleCast.teardown(true);

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(STATE_RESULTS, (Serializable) resultList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        resultList = (List<Spanned>)savedInstanceState.getSerializable(STATE_RESULTS);
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
        searchView.setOnSuggestionListener(new SearchOnSuggestionListener(searchView));

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(bibleCast.getMediaRouteSelector());

        return true;
    }

    public void updateResultView() {
        // Notify the list adapter the data has changed
        resultsAdapter.notifyDataSetChanged();

        // Hide the entry keyboard
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.clearFocus();

        // send results to cast receiver
        bibleCast.sendMessage(HttpUtils.listToJSON(resultList));
    }

    public void updateVersionView() {
        // Notify the list adapter the data has changed
        versionAdapter.notifyDataSetChanged();

        // Set default bible version
        int position = versionAdapter.getPosition(findBibleVersionById(versionList, DEFAULT_VERSION));

        if(position >= 0) {
            versionSpinner.setSelection(position);
        }
    }

    /**
     * Finds a BibleVersion from a list by ID
     * @param versionList List of BibleVersions
     * @param versionId ID of the BibleVersion to find
     * @return Found BibleVersion or null if not found
     */
    private BibleVersion findBibleVersionById(List<BibleVersion> versionList, String versionId) {

        for(BibleVersion bibleVersion : versionList) {
            if(bibleVersion.getId().equalsIgnoreCase(versionId)) {
                return bibleVersion;
            }
        }

        return null;
    }

    public void showResults() {
        if(resultView != null) {
            resultView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Shown results");

            hideGestureView();
        }
    }

    public void hideResults() {
        if(resultView != null) {
            resultView.setVisibility(View.INVISIBLE);
            Log.d(TAG, "Hidden results");

            showGestureView();
        }
    }

    private void showGestureView() {
        if(gestureView != null) {
            gestureView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Shown gestureView");

            if(scrollImageView != null) {
                scrollImageView.setVisibility(View.VISIBLE);
                Log.d(TAG, "Shown scrollImageView");
            }

            // Hide the entry keyboard
            SearchView searchView = (SearchView) findViewById(R.id.searchView);
            searchView.clearFocus();
        }
    }

    private void hideGestureView() {
        if(gestureView != null) {
            gestureView.setVisibility(View.INVISIBLE);
            Log.d(TAG, "Hidden gestureView");
        }

        if(scrollImageView != null) {
            scrollImageView.setVisibility(View.INVISIBLE);
            Log.d(TAG, "Hidden scrollImageView");
        }
    }

    /**
     * Create and show progress dialog (message)
     */
    public void showProgress() {
        progressDialog.setMessage(getText(R.string.ui_wait));
        progressDialog.show();
    }

    /**
     * Close progress dialog
     */
    public void closeDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void sendCastMessage(String message) {
        bibleCast.sendMessage(message);
    }

    public String getBibleVersion() {
        return bibleVersion;
    }

    public void setBibleVersion(String bibleVersion) {
        this.bibleVersion = bibleVersion;
    }

    public List<BibleVersion> getVersions() {
        return versionList;
    }

    public void setVersions(List<BibleVersion> versionList) {
        this.versionList = versionList;
    }

    public boolean isResultsExist() {
        return resultsExist;
    }

    public void setResultsExist(boolean resultsExist) {
        this.resultsExist = resultsExist;
    }

    public List<Spanned> getResults() {
        return resultList;
    }

    public void setResults(List<Spanned> resultList) {
        this.resultList = resultList;
    }

    public List<String> getBooks() {
        return bookList;
    }

    public void setBooks(List<String> bookList) {
        this.bookList = bookList;
    }

    public Activity getActivity() {
        return this;
    }

    private class ScrollOnTouchListener implements View.OnTouchListener {

        private final String TAG = ScrollOnTouchListener.class.getName();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mDetector.onTouchEvent(event);

            Log.d(TAG, "onTouchEvent: " + event.toString());

            return false;
        }
    }
}
