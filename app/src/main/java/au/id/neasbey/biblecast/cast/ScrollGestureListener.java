package au.id.neasbey.biblecast.cast;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import au.id.neasbey.biblecast.BibleSearch;

/**
 * Created by craigneasbey on 1/10/15.
 */
public class ScrollGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final String TAG = ScrollGestureListener.class.getName();

    private BibleSearch bibleSearch;

    public ScrollGestureListener(BibleSearch bibleSearch) {
        this.bibleSearch = bibleSearch;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        Log.d(TAG, "OnScroll: " + distanceY);

        int offSet = Math.round(distanceY);

        if(offSet != 0) {
            bibleSearch.sendCastMessage("{ \"gesture\" : \"scroll\", \"offset\" : \"" + offSet + "\" }");
        }

        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        Log.d(TAG, "onFling: " + velocityY);

        new FlingRunnable(e1, e2, velocityX, velocityY).run();

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
