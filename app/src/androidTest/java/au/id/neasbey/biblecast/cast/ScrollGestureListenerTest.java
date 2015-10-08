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

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOnScroll() throws Exception {

    }

    public void testOnFling() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        MotionEvent e1 = MotionEvent.obtain(146682147, 146682147, MotionEvent.ACTION_DOWN, 385.0f, 116.0f, 0);
        MotionEvent e2 = MotionEvent.obtain(146682147, 146682292, MotionEvent.ACTION_UP, 428.5f, 722.0f, 0);

        //MotionEvent { action=ACTION_DOWN, id[0]=0, x[0]=385.0, y[0]=116.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=146682147, downTime=146682147, deviceId=6, source=0x1002 }
        //MotionEvent { action=ACTION_UP, id[0]=0, x[0]=428.5, y[0]=722.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=146682292, downTime=146682147, deviceId=6, source=0x1002 }
        float velocityX = 1043.3232f;
        float velocityY = 14546.014f;

        long durationMilliSec = 4000;
        int expectedOnScrollCalls = 21;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        velocityY = 3738.125f;
        durationMilliSec = 1000;
        expectedOnScrollCalls += 21;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(durationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArgumentCaptor<String> messageCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast, Mockito.times(expectedOnScrollCalls)).sendMessage(messageCaptor2.capture());

        for (String message : messageCaptor2.getAllValues()) {
            Log.d(TAG, message);
        }
    }
}