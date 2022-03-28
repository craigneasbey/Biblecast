package au.id.neasbey.biblecast.cast;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by craigneasbey on 1/10/15.
 * <p/>
 * Listener for scroll motions on the gesture view during google casting and sends the offset to the cast device
 */
public class ScrollGestureListener extends GestureDetector.SimpleOnGestureListener {

    private final String TAG = ScrollGestureListener.class.getName();

    private BibleCast bibleCast;

    private CastMessageBuffer castMessageBuffer;

    private List<FlingRunnable> flingRunnableList;

    private boolean allowScroll;

    public ScrollGestureListener(BibleCast bibleCast) {
        this.bibleCast = bibleCast;

        final int sendSizeThreshold = 5;
        final long durationMilliSec = 200;
        flingRunnableList = new LinkedList<>();
        allowScroll = true;
        castMessageBuffer = new CastMessageBuffer(sendSizeThreshold, durationMilliSec) {

            @Override
            protected Object concatenateMessages(List<Object> messages) {
                int offSet = 0;

                for (Object newOffSet : messages) {
                    offSet += (int) newOffSet;
                }

                return offSet;
            }

            @Override
            public void sendMessage(Object message) {
                sendJSONMessage((Integer) message);
            }
        };
        castMessageBuffer.start();
    }

    public void sendJSONMessage(int offset) {

        JSONObject jsonMessage = new JSONObject();

        try {
            jsonMessage.put("gesture", "scroll");
            jsonMessage.put("offset", offset);
        } catch (JSONException je) {
            Log.e(TAG, je.getMessage());
        }

        bibleCast.sendMessage(jsonMessage.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        Log.d(TAG, "OnScroll: " + distanceY);

        if (allowScroll || distanceX == 0) {
            int offSet = Math.round(distanceY);

            // if there is a change of at least a pixel, send the scroll off set to cast
            if (offSet != 0) {
                castMessageBuffer.bufferMessage(offSet);
            }
        } else {
            // stops erratic behaviour on cast
            Log.d(TAG, "Disallowed scroll: " + distanceY);
        }

        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        Log.d(TAG, "onFling: " + velocityY);

        FlingRunnable flingRunnable = new FlingRunnableImpl(e1, e2, velocityX, velocityY);
        flingRunnableList.add(flingRunnable);
        allowScroll = false;
        flingRunnable.run();

        return false;
    }

    public boolean onDown(MotionEvent e) {
        if(!flingRunnableList.isEmpty()) {
            cancelFlings();
        }

        return false;
    }

    public void cancelFlings() {
        Log.d(TAG, "cancelFlings");

        for (FlingRunnable flingRunnable : flingRunnableList) {
            if (flingRunnable != null) {
                flingRunnable.cancel();
            }
        }

        flingRunnableList.clear();
        allowScroll = true;
    }

    public void flingComplete(FlingRunnable flingRunnable) {
        Log.d(TAG, "flingComplete");

        flingRunnableList.remove(flingRunnable);

        if(flingRunnableList.isEmpty()) {
            allowScroll = true;
        }
    }

    private class FlingRunnableImpl extends FlingRunnable {

        public FlingRunnableImpl(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            super(e1, e2, velocityX, velocityY);
        }

        @Override
        protected boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return ScrollGestureListener.this.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        protected void flingComplete(FlingRunnable flingRunnable) {
            ScrollGestureListener.this.flingComplete(flingRunnable);
        }
    }
}