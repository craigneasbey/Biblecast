package au.id.neasbey.biblecast.cast;

import android.util.Log;
import android.view.MotionEvent;

import au.id.neasbey.biblecast.util.CountDownTimer;

/**
 * Created by craigneasbey on 16/10/15.  Algorithm by amwaters.
 * <p/>
 * Converts fling motion to decreasing scroll motions
 */
public abstract class FlingRunnable implements Runnable {

    protected final float DECELERATION = 5000f; // px/s/s
    protected final long TICK_TIME_MS = 20; // ~5fps
    private final String TAG = FlingRunnable.class.getName();
    protected MotionEvent e1;
    protected MotionEvent e2;
    protected float velocityY; // px/s
    protected float velocityTicksY; // px/tick
    protected float distanceY;
    protected CountDownTimer countDownTimer;

    public FlingRunnable(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.e1 = e1;
        this.e2 = e2;
        this.velocityY = velocityY;
    }

    @Override
    public void run() {
        float tickTimeSec = (float) TICK_TIME_MS / 1000f;

        // algorithm only works for 1D, need a second timer to do 2D
        velocityTicksY = velocityY * tickTimeSec;

        // Parabolic curve (linear deceleration) - reduces the velocity by a constant amount each tick
        final float decelerationTicksY = DECELERATION * tickTimeSec * tickTimeSec * ((velocityY < 0) ? 1 : -1);
        final long durationMilliSec = (long) (-velocityTicksY / decelerationTicksY) * TICK_TIME_MS;

        Log.d(TAG, "durationMilliSec: " + durationMilliSec);
        final long totalTicks = durationMilliSec / TICK_TIME_MS;
        Log.d(TAG, "totalTicks: " + totalTicks);
        Log.d(TAG, "decelerationTicksY: " + decelerationTicksY);

        countDownTimer = new CountDownTimer(durationMilliSec, TICK_TIME_MS) {
            private float distanceX = 0; // used to show the scroll is from a fling

            public void onTick(long millisUntilFinished) {
                // for testing
                //Log.d(TAG, "millisUntilFinished: " + millisUntilFinished);

                distanceY = velocityTicksY * -1; // invert sign for cast

                onScroll(e1, e2, distanceX, distanceY);
                velocityTicksY += decelerationTicksY;
            }

            public void onFinish() {
                Log.d(TAG, "finish");
                flingComplete(FlingRunnable.this);
            }

        };
        countDownTimer.start();
    }

    public void cancel() {
        countDownTimer.cancel();
    }

    protected abstract boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY);

    protected abstract void flingComplete(FlingRunnable flingRunnable);
}
