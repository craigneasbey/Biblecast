package au.id.neasbey.biblecast.util;

import android.util.Log;

import junit.framework.TestCase;

/**
 * Created by craigneasbey on 2/10/15.
 *
 * Tests the count down timer
 */
public class CountDownTimerTest extends TestCase {

    private final String TAG = CountDownTimerTest.class.getName();

    private int actualTicks;

    private boolean finished;

    public void testCountDownTimer500ms() {
        long durationMilliSec = 500;
        long tickIntervalMilliSec = 50;
        final int expectedTicks = 10;
        setActualTicks(0);
        setFinished(false);

        CountDownTimerImpl objectUnderTest = new CountDownTimerImpl(durationMilliSec, tickIntervalMilliSec);
        objectUnderTest.start();

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(expectedTicks, getActualTicks());
        assertTrue(getFinished());
    }

    public void testCountDownTimer531ms() {
        long durationMilliSec = 531;
        long tickIntervalMilliSec = 7;
        final int expectedTicks = 75;
        setActualTicks(0);
        setFinished(false);

        CountDownTimerImpl objectUnderTest = new CountDownTimerImpl(durationMilliSec, tickIntervalMilliSec);
        objectUnderTest.start();

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(expectedTicks, getActualTicks());
        assertTrue(getFinished());
    }

    public void testCountDownTimer1000ms() {
        long durationMilliSec = 1000;
        long tickIntervalMilliSec = 100;
        final int expectedTicks = 10;
        setActualTicks(0);
        setFinished(false);

        CountDownTimerImpl objectUnderTest = new CountDownTimerImpl(durationMilliSec, tickIntervalMilliSec);
        objectUnderTest.start();

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(expectedTicks, getActualTicks());
        assertTrue(getFinished());
    }

    public void setActualTicks(int ticks) {
        actualTicks = ticks;
    }

    public int getActualTicks() {
        return actualTicks;
    }

    public void addTick() {
        actualTicks++;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean getFinished() {
        return finished;
    }

    private class CountDownTimerImpl extends CountDownTimer {

        public CountDownTimerImpl(long durationMilliSec, long tickIntervalMilliSec) {
            super(durationMilliSec, tickIntervalMilliSec);
        }

        protected void onTick(long millisUntilFinished) {
            Log.d(TAG, "millisUntilFinished: " + millisUntilFinished);
            addTick();
        }

        protected void onFinish() {
            Log.d(TAG, "onFinish");
            setFinished(true);
        }
    }
}