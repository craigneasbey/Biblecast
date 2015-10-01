package au.id.neasbey.biblecast.cast;

import android.os.Bundle;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

import au.id.neasbey.biblecast.BibleSearch;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.exception.BiblecastException;
import au.id.neasbey.biblecast.model.Dimensions;
import au.id.neasbey.biblecast.util.CastUtils;
import au.id.neasbey.biblecast.util.HttpUtils;

/**
 * Created by craigneasbey on 1/10/15.
 *
 * Sets up Google cast media router for device discovery, connection and communication
 */
public class BibleCast {

    private static final String TAG = BibleCast.class.getSimpleName();

    private BibleSearch bibleSearch;

    private MediaRouter mMediaRouter;

    private MediaRouteSelector mMediaRouteSelector;

    private MediaRouter.Callback mMediaRouterCallback;

    private CastDevice mSelectedDevice;

    private GoogleApiClient mApiClient;

    private BiblecastChannel mBiblecastChannel;

    private boolean mApplicationStarted;

    private boolean mWaitingForReconnect;

    private String mSessionId;

    public BibleCast(BibleSearch bibleSearch) {
        this.bibleSearch = bibleSearch;

        // Setup cast device discovery
        mMediaRouter = android.support.v7.media.MediaRouter.getInstance(bibleSearch.getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(bibleSearch.getResources().getString(R.string.app_id))).build();
        mMediaRouterCallback = new BiblecastMediaRouterCallback();
    }

    /**
     * Start media router discovery
     */
    public void startMediaDiscovery() {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    /**
     * End media router discovery
     */
    public void endMediaDiscovery() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    /**
     * Tear down the connection to the receiver
     */
    public void teardown(boolean selectDefaultRoute) {
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

        bibleSearch.showResults();
    }

    public boolean isCastConnected() {
        return mApiClient != null && mBiblecastChannel != null;
    }

    /**
     * Send a text message to the receiver
     */
    public void sendMessage(String message) {
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

    public MediaRouteSelector getMediaRouteSelector() {
        return mMediaRouteSelector;
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

        Cast.Listener mCastListener;
        GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
        GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;

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
            mApiClient = new GoogleApiClient.Builder(bibleSearch)
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
    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

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

                            bibleSearch.hideResults();
                        } catch (IOException e) {
                            // TODO handle exception
                            Log.e(TAG, "Exception while creating channel", e);
                        }
                    }
                } else {
                    // Launch the receiver app
                    Cast.CastApi.launchApplication(mApiClient, bibleSearch.getString(R.string.app_id), false).setResultCallback(
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

                                            bibleSearch.hideResults();
                                        } catch (IOException e) {
                                            // TODO handle exception
                                            Log.e(TAG, "Exception while creating channel", e);
                                        }

                                        // set the initial instructions on the receiver if no results exist
                                        if (bibleSearch.isResultsExist()) {
                                            sendMessage(HttpUtils.listToJSON(bibleSearch.getResults()));
                                        } else {
                                            sendMessage(bibleSearch.getString(R.string.cast_instructions));
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
    private class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

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
            return bibleSearch.getString(R.string.namespace);
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
