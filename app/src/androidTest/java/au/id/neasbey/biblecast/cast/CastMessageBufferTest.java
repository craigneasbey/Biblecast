package au.id.neasbey.biblecast.cast;

import android.util.Log;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by craigneasbey on 16/10/15.
 *
 * Tests the cast message buffer
 */
public class CastMessageBufferTest extends TestCase {

    private final String TAG = CastMessageBufferTest.class.getName();

    private int messageCount;

    private int result;

    private CastMessageBuffer objectUnderTest;

    private List<Object> testBufferlist;

    private void addTestMessage(Object message) {
        result = 0;
        objectUnderTest.bufferMessage(message);
        testBufferlist.add(message);
    }

    private void sendJSONMessage(Integer message) {
        Log.d(TAG, "New message: " + message);
        messageCount++;
        result = message;
    }

    public void testCastMessageBuffer() throws Exception {
        final int sendSizeThreshold = 5;
        final long durationMilliSec = 200;
        final int sumResult = 1;
        messageCount = 0;
        result = 0;
        testBufferlist = new LinkedList<>();

        objectUnderTest = new CastMessageBuffer(sendSizeThreshold, durationMilliSec) {

            @Override
            protected Object concatenateMessages(List<Object> messages) {
                boolean found;

                for (Object message : messages) {
                    found = false;

                    for (int i = sendSizeThreshold; i < testBufferlist.size(); i++) {
                        Object testMessage = testBufferlist.get(i);
                        if (message.equals(testMessage)) {
                            found = true;
                        }
                    }

                    assertTrue(found);
                }

                return sumResult;
            }

            @Override
            public void sendMessage(Object message) {
                sendJSONMessage((Integer)message);
            }
        };
        objectUnderTest.start();

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        addTestMessage(23);
        assertEquals(23, result);
        addTestMessage(2);
        assertEquals(2, result);
        addTestMessage(5);
        assertEquals(5, result);
        addTestMessage(48);
        assertEquals(48, result);
        addTestMessage(78);
        assertEquals(78, result);
        addTestMessage(21);
        addTestMessage(3);
        addTestMessage(7);
        addTestMessage(43);
        addTestMessage(58);
        addTestMessage(39);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec + durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(sumResult, result);
        assertEquals(sendSizeThreshold + 1, messageCount);

        objectUnderTest.cancel();
    }
}
