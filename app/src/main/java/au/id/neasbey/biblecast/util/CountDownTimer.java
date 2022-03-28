package au.id.neasbey.biblecast.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by craigneasbey on 2/10/15.
 * <p/>
 * Abstract count down timer that ticks at each interval.
 * <p/>
 * Example:
 * <pre class="prettyprint">
 * new CountDownTimer(durationMilliSec, tickIntervalMilliSec) {
 *
 *     {@literal @}Override
 *     public void onTick(long millisUntilFinished) {
 *         Log.d(TAG, "seconds remaining: " + millisUntilFinished / 1000);
 *     }
 *
 *     {@literal @}Override
 *     public void onFinish() {
 *         Log.d(TAG, "finish");
 *     }
 * }.start();
 * </pre>
 */
public abstract class CountDownTimer {

    private long interval;

    private Timer timer;

    private long tickIntervalMilliSec;

    private boolean finished;

    public CountDownTimer(long durationMilliSec, long tickIntervalMilliSec) {
        this.interval = durationMilliSec;
        this.tickIntervalMilliSec = tickIntervalMilliSec;
        timer = new Timer();
        finished = false;
    }

    /**
     * Schedules the task to run now
     */
    public void start() {
        int delay = 0;

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                onTick(decreaseInterval());

                if (finished) {
                    onFinish();
                }
            }
        }, delay, tickIntervalMilliSec);
    }

    /**
     * Decreases the interval by a tick until it reaches 0.
     *
     * @return The new interval value after the tick
     */
    private long decreaseInterval() {
        interval -= tickIntervalMilliSec;

        if (interval - tickIntervalMilliSec < 0) {
            timer.cancel();

            finished = true;
        }

        return interval;
    }

    /**
     * Occurs on each interval (tick)
     *
     * @param millisUntilFinished The amount of milliseconds until finished.
     */
    protected abstract void onTick(long millisUntilFinished);

    /**
     * Occurs after the last tick
     */
    protected abstract void onFinish();

    /**
     * Cancel the count down
     */
    public void cancel() {
        timer.cancel();
    }
}