package au.id.neasbey.biblecast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BiblesOrg.BibleAPIBiblesOrg;
import au.id.neasbey.biblecast.API.BiblesOrg.BibleAPIConnectionHandlerBiblesOrg;
import au.id.neasbey.biblecast.API.BiblesOrg.BibleAPIResponseParserBiblesOrg;
import au.id.neasbey.biblecast.API.BibleAPIQueryType;
import au.id.neasbey.biblecast.model.BibleVersion;
import au.id.neasbey.biblecast.model.Dimensions;
import au.id.neasbey.biblecast.util.CastUtils;
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

    private static final String RESULTS = "results";

    private boolean resultsExist;

    private String bibleVersion;

    private List<BibleVersion> versionList;

    private ArrayAdapter<BibleVersion> versionAdapter;

    private List<String> bookList;

    private List<Spanned> resultList;

    private ArrayAdapter<Spanned> resultsAdapter;

    private ListView resultView;

    private GestureOverlayView gestureView;

    private ImageView scrollImageView;

    protected GestureDetectorCompat mDetector;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private Cast.Listener mCastListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private BiblecastChannel mBiblecastChannel;
    private boolean mApplicationStarted;
    private boolean mWaitingForReconnect;
    private String mSessionId;

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
            resultList = (List<Spanned>) savedInstanceState.getSerializable(RESULTS);
        }

        setContentView(R.layout.activity_bible_search);
        UIUtils.setContext(this);

        setupResultsView();
        setupGestureView();
        setupVersionSpinner();
        setupCast();

        getBibleVersions();
        getBookSuggestions();
    }

    private void setupVersionSpinner() {
        // may need this: http://stackoverflow.com/questions/1625249/android-how-to-bind-spinner-to-custom-object-list
        versionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, versionList);
        versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.versionSpinner);
        spinner.setAdapter(versionAdapter);
        spinner.setOnItemSelectedListener(new SearchOnItemSelectedListener());
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
        mDetector = new GestureDetectorCompat(this, new ScrollGestureListener());

        scrollImageView = (ImageView) findViewById(R.id.scrollImageView);
    }

    /**
     * Setup cast device discovery
     */
    protected void setupCast() {
        mMediaRouter = android.support.v7.media.MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(getResources()
                        .getString(R.string.app_id))).build();
        mMediaRouterCallback = new BiblecastMediaRouterCallback();
    }

    private void getBibleVersions() {
        new BibleAPITask().execute(BibleAPIQueryType.VERSION.name(), getText(R.string.api_versions_url).toString(), LANGUAGE_ENG_US);
    }

    private void getBookSuggestions() {
        new BibleAPITask().execute(BibleAPIQueryType.BOOK.name(), getText(R.string.api_books_url).toString());
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
                new BibleAPITask().execute(BibleAPIQueryType.SEARCH.name(), getText(R.string.api_search_url).toString(), query.toLowerCase(), bibleVersion);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        // Start media router discovery
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        // End media router discovery
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        // Close cast connection
        teardown(true);

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(RESULTS, (Serializable) resultList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        resultList = (List<Spanned>)savedInstanceState.getSerializable(RESULTS);
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
        searchView.setOnSuggestionListener(new SearchOnSuggestionListener());

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

        return true;
    }

    private Activity getActivity() {
        return this;
    }

    private class SearchOnSuggestionListener implements SearchView.OnSuggestionListener {

        @Override
        public boolean onSuggestionSelect(int position) {
            return updateSearchText(position);
        }

        @Override
        public boolean onSuggestionClick(int position) {
            return updateSearchText(position);
        }

        public boolean updateSearchText(int position) {
            // http://ramannanda.blogspot.com.au/2014/10/android-searchview-integration-with.html
            SearchView searchView = (SearchView) findViewById(R.id.searchView);

            Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
            String feedName = cursor.getString(1); // column SearchManager.SUGGEST_COLUMN_TEXT_1
            searchView.setQuery(feedName, false);

            return true;
        }
    }

    private class SearchOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected (AdapterView < ? > parent, View view,int position, long id) {
            bibleVersion = ((BibleVersion) parent.getItemAtPosition(position)).getId();
            Log.d(TAG, "BibleVersion: " + bibleVersion);
        }

        @Override
        public void onNothingSelected (AdapterView < ? > parent){
        int selected = 0;

        for (int i = 0; i < versionList.size(); i++) {
            BibleVersion version = versionList.get(i);
            if (version.getId().equalsIgnoreCase(bibleVersion)) {
                selected = i;
            }
        }

        parent.setSelection(selected);

        if (selected == 0 && versionList.size() > 0) {
            bibleVersion = versionList.get(0).getId();
            Log.d(TAG, "BibleVersion: " + bibleVersion);
        }
        }
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

    private class ScrollGestureListener extends GestureDetector.SimpleOnGestureListener {
        private final String TAG = ScrollGestureListener.class.getName();

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.d(TAG, "OnScroll: " + distanceY);

            int offSet = Math.round(distanceY);

            if(offSet != 0) {
                sendMessage("{ \"gesture\" : \"scroll\", \"offset\" : \"" + offSet + "\" }");
            }

            return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.d(TAG, "onFling: " + velocityY);

            FlingRunnable fr = new FlingRunnable(e1, e2, velocityX, velocityY);

            return false;
        }

        private class FlingRunnable implements Runnable {

            protected MotionEvent e1;
            protected MotionEvent e2;
            protected float velocityX;
            protected float velocityY;
            protected float distanceX;
            protected float distanceY;


            public FlingRunnable(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                this.e1 = e1;
                this.e2 = e2;
                this.velocityX = velocityX;
                this.velocityY = velocityY;
            }

            @Override
            public void run() {
                // TODO needs tuning
                distanceX = velocityX * -1 / 20;
                distanceY = velocityY * -1 / 20;

                new CountDownTimer((long)Math.abs(velocityY) / 8, 10) {

                    public void onTick(long millisUntilFinished) {
                        onScroll(e1, e2, distanceX, distanceY);
                        distanceY -= distanceY / 10;
                    }

                    public void onFinish() {
                        onScroll(e1, e2, distanceX, distanceY);
                    }
                }.start();
            }
        }
    }

    /**
     * Connects to the Bible REST JSON web service, performs a search query, updates the activity with the response JSON
     */
    private class BibleAPITask extends AsyncTask<String, String, String> {

        private BibleAPI bibleAPI;

        private final ProgressDialog progressDialog = new ProgressDialog(BibleSearch.this);

        private BibleAPIQueryType queryType;

        protected void onPreExecute() {
            setupBibleAPI();

            // Start Progress Dialog (Message)
            progressDialog.setMessage(getText(R.string.ui_wait));
            progressDialog.show();
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
            bibleAPI.setUsername(getText(R.string.api_username).toString());
            bibleAPI.setPassword(getText(R.string.api_password).toString());
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

            // Close progress dialog
            progressDialog.dismiss();

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

                UIUtils.displayError(getActivity(), R.string.api_failed, getText(R.string.ok).toString(), result);
            } else if (TextUtils.isEmpty(result) && bibleAPI.hasResults()) {
                // If result test is empty and there are results, update the results list displayed
                Log.d(TAG, "Request Successful");

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

        private void handleSearchResults() {

            resultsExist = true;
            bibleAPI.updateResultList(resultList);

            // Notify the list adapter the data has changed
            resultsAdapter.notifyDataSetChanged();

            // Hide the entry keyboard
            SearchView searchView = (SearchView) findViewById(R.id.searchView);
            searchView.clearFocus();

            // send results to cast receiver
            sendMessage(HttpUtils.listToJSON(resultList));
        }

        private void handleVersionResults() {

            bibleAPI.updateResultList(versionList);

            // Notify the list adapter the data has changed
            versionAdapter.notifyDataSetChanged();
        }

        private void handleBookResults() {

            bibleAPI.updateResultList(bookList);

            new SuggestionRunnable(bookList, getContentResolver());
        }

        @Override
        protected void onCancelled() {
            closeDialog();
        }

        /**
         * Close progress dialog
         */
        private void closeDialog() {
            if(progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        private class SuggestionRunnable implements Runnable {
            List<String> suggestions;
            ContentResolver contentResolver;
            ContentValues contentValues;

            SuggestionRunnable(List<String> suggestions, ContentResolver contentResolver) {
                this.suggestions = suggestions;
                this.contentResolver = contentResolver;
            }

            public void run() {
                // TODO make more efficient by comparing, then adding and deleting as needed
                // http://stackoverflow.com/questions/13733460/android-providing-recent-search-suggestions-without-searchable-activity
                removeAllSuggestions();


                for(String suggestion : suggestions) {
                    addSuggestion(suggestion);
                }
            }

            private void removeAllSuggestions() {
                if(contentResolver != null) {
                    contentResolver.delete(SearchSuggestionProvider.CONTENT_URI, null, null);
                }
            }

            private void removeSuggestion(String suggestion) {
                if(contentResolver != null) {
                    contentResolver.delete(SearchSuggestionProvider.CONTENT_URI, suggestion, null);
                }
            }

            private void addSuggestion(String suggestion) {
                if(contentValues == null) {
                    contentValues = new ContentValues();
                }

                contentValues.clear();
                contentValues.put(SearchSuggestionProvider.SUGGESTION, suggestion);

                if(contentResolver != null) {
                    contentResolver.insert(SearchSuggestionProvider.CONTENT_URI, contentValues);
                }
            }

            private void checkSuggestion(String suggestion) {
                // TODO implement
                if(contentResolver != null) {
                    contentResolver.query(SearchSuggestionProvider.CONTENT_URI, null, null, null, null);
                }
            }
        }
    }

    /**
     * Tear down the connection to the receiver
     */
    private void teardown(boolean selectDefaultRoute) {
        Log.d(TAG, "teardown");
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                    try {
                        Cast.CastApi.stopApplication(mApiClient, mSessionId);
                        if (mBiblecastChannel != null) {
                            Cast.CastApi.removeMessageReceivedCallbacks(
                                    mApiClient,
                                    mBiblecastChannel.getNamespace());
                            mBiblecastChannel = null;
                        }
                    } catch (IOException e) {
                        // TODO handle exception
                        Log.e(TAG, "Exception while removing channel", e);
                    }
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        if (selectDefaultRoute) {
            mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;

        showResults();
    }

    private void showResults() {
        if(resultView != null) {
            resultView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Shown results");

            hideGestureView();
        }
    }

    private void hideResults() {
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

    private boolean isCastConnected() {
        return mApiClient != null && mBiblecastChannel != null;
    }

    /**
     * Send a text message to the receiver
     */
    private void sendMessage(String message) {
        if (mApiClient != null && mBiblecastChannel != null) {
            Log.d(TAG, "Sending message: " + message);

            try {
                Cast.CastApi.sendMessage(mApiClient,
                        mBiblecastChannel.getNamespace(), message).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                    Log.e(TAG, "Sending message failed");
                                }
                            }
                        });
            } catch (Exception e) {
                // TODO handle exception
                Log.e(TAG, "Exception while sending message", e);
            }
        } else {
            //Toast.makeText(BibleSearch.this, message, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Disconnected message: " + message);
        }
    }

    private void parseMessage(String message) {

        if(!TextUtils.isEmpty(message)) {

            try {
                // Is currently not used. Demonstrates multi-direction JSON communication with google cast
                Dimensions dimensions = CastUtils.parseMessageForDimensions(message);
            } catch (BiblecastException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    /**
     * Callback for MediaRouter events
     */
    private class BiblecastMediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteSelected");
            // Handle the user route selection.
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            launchReceiver();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteUnselected: info=" + info);
            teardown(false);
            mSelectedDevice = null;
        }
    }

    /**
     * Start the receiver app
     */
    private void launchReceiver() {
        try {
            mCastListener = new Cast.Listener() {

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d(TAG, "application has stopped");
                    teardown(true);
                }

            };
            // Connect to Google Play services
            mConnectionCallbacks = new ConnectionCallbacks();
            mConnectionFailedListener = new ConnectionFailedListener();
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(mSelectedDevice, mCastListener);
            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            // TODO handle exception
            Log.e(TAG, "Failed launchReceiver", e);
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "onConnected");

            if (mApiClient == null) {
                // We got disconnected while this runnable was pending execution.
                return;
            }

            try {
                if (mWaitingForReconnect) {
                    mWaitingForReconnect = false;

                    // Check if the receiver app is still running
                    if ((connectionHint != null)
                            && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                        Log.d(TAG, "Application is no longer running");
                        teardown(true);
                    } else {
                        // Re-create the custom message channel
                        try {
                            Cast.CastApi.setMessageReceivedCallbacks(
                                    mApiClient,
                                    mBiblecastChannel.getNamespace(),
                                    mBiblecastChannel);

                            hideResults();
                        } catch (IOException e) {
                            // TODO handle exception
                            Log.e(TAG, "Exception while creating channel", e);
                        }
                    }
                } else {
                    // Launch the receiver app
                    Cast.CastApi.launchApplication(mApiClient, getString(R.string.app_id), false).setResultCallback(
                            new ResultCallback<Cast.ApplicationConnectionResult>() {
                                @Override
                                public void onResult(Cast.ApplicationConnectionResult result) {

                                    Status status = result.getStatus();
                                    Log.d(TAG, "ApplicationConnectionResultCallback.onResult:" + status.getStatusCode());

                                    if (status.isSuccess()) {
                                        ApplicationMetadata applicationMetadata = result.getApplicationMetadata();
                                        mSessionId = result.getSessionId();
                                        String applicationStatus = result.getApplicationStatus();
                                        boolean wasLaunched = result.getWasLaunched();

                                        Log.d(TAG, "Application name: " + applicationMetadata.getName() + ", status: " + applicationStatus + ", sessionId: " + mSessionId + ", wasLaunched: " + wasLaunched);
                                        mApplicationStarted = true;

                                        // Create the custom message channel
                                        mBiblecastChannel = new BiblecastChannel();
                                        try {
                                            Cast.CastApi.setMessageReceivedCallbacks(mApiClient, mBiblecastChannel.getNamespace(), mBiblecastChannel);

                                            hideResults();
                                        } catch (IOException e) {
                                            // TODO handle exception
                                            Log.e(TAG, "Exception while creating channel", e);
                                        }

                                        // set the initial instructions on the receiver if no results exist
                                        if (resultsExist) {
                                            sendMessage(HttpUtils.listToJSON(resultList));
                                        } else {
                                            sendMessage(getString(R.string.cast_instructions));
                                        }
                                    } else {
                                        Log.e(TAG, "Application could not launch");
                                        teardown(true);
                                    }
                                }
                            });
                }
            } catch (Exception e) {
                // TODO handle exception
                Log.e(TAG, "Failed to launch application", e);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "onConnectionSuspended");
            mWaitingForReconnect = true;
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.e(TAG, "onConnectionFailed ");

            teardown(false);
        }
    }

    /**
     * Custom message channel
     */
    class BiblecastChannel implements Cast.MessageReceivedCallback {

        /**
         * @return custom namespace
         */
        public String getNamespace() {
            return getString(R.string.namespace);
        }

        /*
         * Receive message from the receiver app
         */
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace,
                                      String message) {
            Log.d(TAG, "onMessageReceived: " + message);

            if(castDevice.equals(mSelectedDevice) && namespace.equals(getNamespace())) {
                parseMessage(message);
            }
        }

    }
}
