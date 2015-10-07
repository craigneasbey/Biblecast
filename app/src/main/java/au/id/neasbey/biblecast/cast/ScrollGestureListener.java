package au.id.neasbey.biblecast.cast;

//import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.logging.XMLFormatter;

import au.id.neasbey.biblecast.util.CountDownTimer;

/**
 * Created by craigneasbey on 1/10/15.
 *
 * Listener for scroll motions on the gesture view during google casting and sends the offset to the cast device
 */
public class ScrollGestureListener extends GestureDetector.SimpleOnGestureListener {

    private final String TAG = ScrollGestureListener.class.getName();

    private BibleCast bibleCast;

    private boolean allowScroll;

    public ScrollGestureListener(BibleCast bibleCast) {
        this.bibleCast = bibleCast;

        allowScroll = true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        Log.d(TAG, "OnScroll: " + distanceY);

        if(allowScroll || distanceX == 0) {
            int offSet = Math.round(distanceY);

            // if there is a change more than a pixel, send the scroll off set to cast
            if (offSet != 0) {
                bibleCast.sendMessage("{ \"gesture\" : \"scroll\", \"offset\" : \"" + offSet + "\" }");
            }
        } else {
            Log.d(TAG, "Disallowed scroll: " + distanceY);
        }

        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        Log.d(TAG, "onFling: " + velocityY);

        new FlingRunnable(e1, e2, velocityX, velocityY).run();

        return false;
    }

    /**
     * Converts fling motion to decreasing scroll motions
     */
    private class FlingRunnable implements Runnable {

        protected MotionEvent e1;
        protected MotionEvent e2;
        protected float velocityX;
        protected float velocityY;
        protected float distanceX;
        protected float distanceY;


        public FlingRunnable(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            this.e1 = e1;
            this.e2 = e2;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
        }

        @Override
        public void run() {
            // TODO needs tuning
            final int durationRatio = 4;
            final int reductionRatio = 5;

            allowScroll = false;
            distanceX = 0; // not used
            distanceY = velocityY * -1 / 10; // invert sign

            long durationMilliSec = (long) Math.abs(velocityY) / durationRatio;
            int tickIntervalMilliSec = (int)durationMilliSec / reductionRatio / durationRatio;

            Log.d(TAG, "durationMilliSec: " + durationMilliSec);
            Log.d(TAG, "tickIntervalMilliSec: " + tickIntervalMilliSec);

            new CountDownTimer(durationMilliSec, tickIntervalMilliSec) {

                public void onTick(long millisUntilFinished) {
                    // for testing
                    //Log.d(TAG, "millisUntilFinished: " + millisUntilFinished);
                    onScroll(e1, e2, distanceX, distanceY);
                    distanceY -= distanceY / reductionRatio;
                }

                public void onFinish() {
                    onScroll(e1, e2, distanceX, distanceY);
                    Log.d(TAG, "finish");

                    allowScroll = true;
                }
            }.start();
        }
    }
}
