package au.id.neasbey.biblecast.cast;

import android.util.Log;
import android.view.MotionEvent;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;

/**
 * Created by craigneasbey on 2/10/15.
 *
 * Test the scroll and fling gesture output to google cast
 */
public class ScrollGestureListenerTest extends TestCase {

    private final String TAG = ScrollGestureListenerTest.class.getName();

    private MotionEvent e1;

    private MotionEvent e2;

    public void setUp() throws Exception {
        super.setUp();

        //MotionEvent { action=ACTION_DOWN, id[0]=0, x[0]=385.0, y[0]=116.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=146682147, downTime=146682147, deviceId=6, source=0x1002 }
        //MotionEvent { action=ACTION_UP, id[0]=0, x[0]=428.5, y[0]=722.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=146682292, downTime=146682147, deviceId=6, source=0x1002 }
        e1 = MotionEvent.obtain(146682147, 146682147, MotionEvent.ACTION_DOWN, 385.0f, 116.0f, 0);
        e2 = MotionEvent.obtain(146682147, 146682292, MotionEvent.ACTION_UP, 428.5f, 722.0f, 0);
    }

    public void testOnScroll() throws Exception {

    }

    public void testOnFlingPositiveLarge() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);
        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        float velocityX = 1043.3232f;
        float velocityY = 14546.014f;

        long expectedMaxDurationMilliSec = 4000;
        int expectedOnScrollCalls = 89;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(expectedMaxDurationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast, Mockito.times(expectedOnScrollCalls)).sendMessage(messageCaptor.capture());

        for (String message : messageCaptor.getAllValues()) {
            Log.d(TAG, message);
        }
    }

    public void testOnFlingPositiveSmall() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);
        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        float velocityX = 1043.3232f;
        float velocityY = 3738.125f;

        long expectedMaxDurationMilliSec = 1000;
        int expectedOnScrollCalls = 25;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(expectedMaxDurationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast, Mockito.times(expectedOnScrollCalls)).sendMessage(messageCaptor.capture());

        for (String message : messageCaptor.getAllValues()) {
            Log.d(TAG, message);
        }
    }

    public void testOnFlingNegativeLarge() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);
        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        float velocityX = 1043.3232f;
        float velocityY = -14546.014f;

        long expectedMaxDurationMilliSec = 4000;
        int expectedOnScrollCalls = 89;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(expectedMaxDurationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast, Mockito.times(expectedOnScrollCalls)).sendMessage(messageCaptor.capture());

        for (String message : messageCaptor.getAllValues()) {
            Log.d(TAG, message);
        }
    }
}