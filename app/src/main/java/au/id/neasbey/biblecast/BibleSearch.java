package au.id.neasbey.biblecast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIBibleOrg;
import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIConnectionHandlerBibleOrg;
import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIResponseParserBibleOrg;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 30/06/15.
 *
 * Activity that allows the user to enter bible search term and return results from a web service
 */
public class BibleSearch extends AppCompatActivity {

    private static final String TAG = BibleSearch.class.getSimpleName();

    private String apiURL;
    private String apiUsername;
    private String apiPassword;
    private boolean resultsExist = false;

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

    private List<Spanned> resultList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_search);
        UIUtils.setContext(this);

        // Configure Bible API
        apiURL = getText(R.string.api_url).toString();
        apiUsername = getText(R.string.api_username).toString();
        apiPassword = getText(R.string.api_password).toString();

        // Configure Cast device discovery
        mMediaRouter = android.support.v7.media.MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(getResources()
                        .getString(R.string.app_id))).build();
        mMediaRouterCallback = new BiblecastMediaRouterCallback();

        // Initial application search
        handleIntent(getIntent());
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
            String query = intent.getStringExtra(SearchManager.QUERY);
            new BibleAPITask().execute(query, apiURL, apiUsername, apiPassword);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start media router discovery
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        // End media router discovery
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        teardown(true);
        super.onDestroy();
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

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

        return true;
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
            // TODO improve scrolling http://stackoverflow.com/questions/16791100/detect-scroll-up-scroll-down-in-listview
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                private int mLastFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    Log.d(TAG," onScroll firstVisibleItem: " + firstVisibleItem + " visibleItemCount: " + visibleItemCount + " totalItemCount: " + totalItemCount);

                    if(mLastFirstVisibleItem < firstVisibleItem)
                    {
                        Log.d(TAG,"SCROLLING DOWN");
                        sendMessage("#GESTURE#SCROLL_DOWN#");
                    }

                    if(mLastFirstVisibleItem > firstVisibleItem)
                    {
                        Log.d(TAG,"SCROLLING UP");
                        sendMessage("#GESTURE#SCROLL_UP#");
                    }

                    mLastFirstVisibleItem = firstVisibleItem;
                }
            });
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

            // Specifies Bible.org Bible API
            BibleAPIConnectionHandler bibleAPIConnectionHandler = new BibleAPIConnectionHandlerBibleOrg();
            BibleAPIResponseParser bibleAPIResponseParser = new BibleAPIResponseParserBibleOrg();

            bibleAPI = new BibleAPIBibleOrg(bibleAPIConnectionHandler, bibleAPIResponseParser);
            bibleAPI.setQuery(params[0]);
            bibleAPI.setURL(params[1]);
            bibleAPI.setUsername(params[2]);
            bibleAPI.setPassword(params[3]);
            bibleAPI.setVersions(bibleVersions);

            return bibleAPI.query();
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

                resultsExist = true;
                bibleAPI.updateResultList(resultList);

                // send results to cast receiver
                // TODO convert list to JSON
                // http://json2html.com/
                // http://stackoverflow.com/questions/4189365/use-jquery-to-convert-json-array-to-html-bulleted-list
                // http://stackoverflow.com/questions/8434579/how-to-parse-json-into-nested-html-list-strucuture
                sendMessage(resultList.toString());

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
                        // TODO // FIXME: 17/08/15
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
                Log.e(TAG, "Exception while sending message", e);
            }
        } else {
            Toast.makeText(BibleSearch.this, message, Toast.LENGTH_SHORT).show();
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
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastListener);
            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
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
                // We got disconnected while this runnable was pending
                // execution.
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
                        } catch (IOException e) {
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
                                        } catch (IOException e) {
                                            Log.e(TAG, "Exception while creating channel",
                                                    e);
                                        }

                                        // set the initial instructions on the receiver if no results exist
                                        if (resultsExist) {
                                            // TODO convert list to JSON
                                            sendMessage(resultList.toString());
                                        } else {
                                            sendMessage(getString(R.string.instructions));
                                        }
                                    } else {
                                        Log.e(TAG, "Application could not launch");
                                        teardown(true);
                                    }
                                }
                            });
                }
            } catch (Exception e) {
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
        }

    }
}
