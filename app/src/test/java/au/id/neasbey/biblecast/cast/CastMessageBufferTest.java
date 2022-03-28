package au.id.neasbey.biblecast.cast;

import android.util.Log;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.BuildConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by craigneasbey on 16/10/15.
 *
 * Tests the cast message buffer
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class CastMessageBufferTest {

    private final String TAG = CastMessageBufferTest.class.getName();

    private int messagesSinceReset;

    private int result;

    private final int sendSizeThreshold = 5;

    private final int sumResult = -1;

    private List<Object> testBufferlist;

    private CastMessageBuffer objectUnderTest;

    @BeforeClass
    public static void setupRobolectric() {
        ShadowLog.stream = System.out;
    }

    private void addTestMessage(Object message) {
        result = -1;
        testBufferlist.add(message);
        objectUnderTest.bufferMessage(message);
    }

    private void sendJSONMessage(Integer message) {
        Log.d(TAG, "New message: " + message);
        assertTrue(messagesSinceReset <= sendSizeThreshold);
        messagesSinceReset++;
        testBufferlist.remove(message);
        result = message;
    }

    @Test
    public void shouldSuccessfullyBufferCastMessages() throws Exception {
        final long durationMilliSec = 200;
        final int[] testMessages = { 23, 2, 5, 48, 78, 21, 3, 7, 43, 58, 39, 54, 72, 94, 34, 22, 14, 71, 77, 96, 41, 82 };
        messagesSinceReset = 0;
        result = 0;
        testBufferlist = new LinkedList<>();

        objectUnderTest = new CastMessageBuffer(sendSizeThreshold, durationMilliSec) {

            @Override
            protected Object concatenateMessages(List<Object> messages) {
                boolean found;

                assertTrue(testBufferlist.size() >= messages.size());

                for(int i=0; i < testBufferlist.size(); i++) {
                    assertEquals(testBufferlist.get(i), messages.get(i));
                }

                return sumResult;
            }

            @Override
            public void sendMessage(Object message) {
                sendJSONMessage((Integer)message);
            }

            @Override
            public void beforeBufferReset(int sendSize) {
                Log.d(TAG, "beforeBufferReset: " + sendSize);
                assertTrue(sendSize <= sendSizeThreshold);

                messagesSinceReset = 0;
            }

            @Override
            public void afterBufferReset() {
                Log.d(TAG, "afterBufferReset");

                checkResult(sumResult, result);
            }
        };
        objectUnderTest.start();

        for(Object message: testMessages) {
            addTestMessage(message);
            checkResult((int) message, result);
        }

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec + durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        objectUnderTest.cancel();
    }

    private void checkResult(int expected, int actual) {
        if(actual != -1) {
            assertEquals(expected, actual);
        }
    }
}
