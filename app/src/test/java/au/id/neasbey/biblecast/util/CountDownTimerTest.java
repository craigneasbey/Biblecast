package au.id.neasbey.biblecast.util;

import android.util.Log;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import au.id.neasbey.biblecast.BuildConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by craigneasbey on 2/10/15.
 *
 * Tests the count down timer
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class CountDownTimerTest {

    private final String TAG = CountDownTimerTest.class.getName();

    private int actualTicks;

    private boolean finished;

    @BeforeClass
    public static void setupRobolectric() {
        ShadowLog.stream = System.out;
    }

    @Test
    public void shouldSuccessfullyCountDown500ms() {
        long durationMilliSec = 500;
        long tickIntervalMilliSec = 50;
        final int expectedTicks = 10;
        setActualTicks(0);
        setFinished(false);

        TestCountDownTimerImpl objectUnderTest = new TestCountDownTimerImpl(durationMilliSec, tickIntervalMilliSec);
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

    @Test
    public void shouldSuccessfullyCountDown531ms() {
        long durationMilliSec = 531;
        long tickIntervalMilliSec = 7;
        final int expectedTicks = 75;
        setActualTicks(0);
        setFinished(false);

        TestCountDownTimerImpl objectUnderTest = new TestCountDownTimerImpl(durationMilliSec, tickIntervalMilliSec);
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

    @Test
    public void shouldSuccessfullyCountDown1000ms() {
        long durationMilliSec = 1000;
        long tickIntervalMilliSec = 100;
        final int expectedTicks = 10;
        setActualTicks(0);
        setFinished(false);

        TestCountDownTimerImpl objectUnderTest = new TestCountDownTimerImpl(durationMilliSec, tickIntervalMilliSec);
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

    private class TestCountDownTimerImpl extends CountDownTimer {

        public TestCountDownTimerImpl(long durationMilliSec, long tickIntervalMilliSec) {
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
