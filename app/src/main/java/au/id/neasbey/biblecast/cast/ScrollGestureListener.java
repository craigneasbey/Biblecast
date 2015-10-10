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

		protected static final float DECELERATION   = 300f; // px/s/s
		protected static final int   TICK_TIME_MS   = 17; // ~60fps
		protected static final float VELOCITY_SCALE = 1f; // scaling factor to turn velocityX/Y into px/sec

        protected MotionEvent e1;
        protected MotionEvent e2;
        protected float velocityY; // px/tick
		protected bool isCancelled = false;

        public FlingRunnable(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            this.e1 = e1;
            this.e2 = e2;
			// AW: algorithm only works for 1D, need a second timer to do 2D
            this.velocityY = velocityY * VELOCITY_SCALE * TICK_TIME_MS / 1000;
        }

		public void cancel() {
			isCancelled = true;
			allowScroll = true;
		}

        @Override
        public void run() {

			// Parabolic curve (linear deceleration)
			// - reduces the velocity by a constant amount each tick

			final int totalTicks = duration_ms / TICK_TIME_MS;
			final float decelerationTicksY = DECELERATION * TICK_TIME_MS * TICK_TIME_MS
					* ((velocityY < 0) ? 1 : -1);
			final float durationY_ms = -velocityY / decelerationTicksY;

			isCancelled = false;
            allowScroll = false;

            new CountDownTimer(durationY_ms, TICK_TIME_MS) {

                public void onTick(long millisUntilFinished) {
					if (!isCancelled)
					{
						onScroll(e1, e2, 0, velocityY); // already in px/tick
						velocityY -= decelerationTicksY;
					}
                }

                public void onFinish() {
                    Log.d(TAG, "finish");
                    allowScroll = true;
                }

            }.start();
        }
    }
}
