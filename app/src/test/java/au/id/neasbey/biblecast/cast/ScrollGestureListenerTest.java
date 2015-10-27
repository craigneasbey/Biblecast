package au.id.neasbey.biblecast.cast;

import android.util.Log;
import android.view.MotionEvent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

import au.id.neasbey.biblecast.BuildConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

/**
 * Created by craigneasbey on 2/10/15.
 *
 * Test the scroll_svg and fling gesture output to google cast
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ScrollGestureListenerTest {

    private final String TAG = ScrollGestureListenerTest.class.getName();

    private MotionEvent e1;

    private MotionEvent e2;

    @BeforeClass
    public static void setupRobolectric() {
        ShadowLog.stream = System.out;
    }

    @Before
    public void setUp() throws Exception {
        //MotionEvent { action=ACTION_DOWN, id[0]=0, x[0]=385.0, y[0]=116.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=146682147, downTime=146682147, deviceId=6, source=0x1002 }
        //MotionEvent { action=ACTION_UP, id[0]=0, x[0]=428.5, y[0]=722.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=146682292, downTime=146682147, deviceId=6, source=0x1002 }
        e1 = MotionEvent.obtain(146682147, 146682147, MotionEvent.ACTION_DOWN, 385.0f, 116.0f, 0);
        e2 = MotionEvent.obtain(146682147, 146682292, MotionEvent.ACTION_UP, 428.5f, 722.0f, 0);
    }

    @Test
    public void shouldSuccessfullyPerformScroll() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);
        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        float distanceX = 20;
        float distanceY = -34;

        objectUnderTest.onScroll(e1, e2, distanceX, distanceY);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast).sendMessage(messageCaptor.capture());

        List<String> values = messageCaptor.getAllValues();

        assertTrue(values.size() == 1);

        assertEquals(values.get(0), "{\"gesture\":\"scroll_svg\",\"offset\":-34}");
    }

    @Test
    public void shouldSuccessfullyPerformLargePositiveFling() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);
        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        float velocityX = 1043.3232f;
        float velocityY = 14546.014f;

        long expectedMaxDurationMilliSec = 4000;
        int expectedMinOnScrollCalls = 74;
        int expectedMaxOnScrollCalls = 77;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(expectedMaxDurationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast, Mockito.atLeast(expectedMinOnScrollCalls)).sendMessage(messageCaptor.capture());
        Mockito.verify(mockBibleCast, Mockito.atMost(expectedMaxOnScrollCalls)).sendMessage(messageCaptor.capture());

        for (String message : messageCaptor.getAllValues()) {
            Log.d(TAG, message);
        }
    }

    @Test
    public void shouldSuccessfullyPerformSmallPositiveFling() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);
        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        float velocityX = 1043.3232f;
        float velocityY = 3738.125f;

        long expectedMaxDurationMilliSec = 1000;
        int expectedMinOnScrollCalls = 20;
        int expectedMaxOnScrollCalls = 22;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(expectedMaxDurationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast, Mockito.atLeast(expectedMinOnScrollCalls)).sendMessage(messageCaptor.capture());
        Mockito.verify(mockBibleCast, Mockito.atMost(expectedMaxOnScrollCalls)).sendMessage(messageCaptor.capture());

        for (String message : messageCaptor.getAllValues()) {
            Log.d(TAG, message);
        }
    }

    @Test
    public void shouldSuccessfullyPerformLargeNegativeFling() throws Exception {
        BibleCast mockBibleCast = Mockito.mock(BibleCast.class);
        Mockito.doNothing().when(mockBibleCast).sendMessage(any(String.class));

        ScrollGestureListener objectUnderTest = new ScrollGestureListener(mockBibleCast);

        float velocityX = 1043.3232f;
        float velocityY = -14546.014f;

        long expectedMaxDurationMilliSec = 4000;
        int expectedMinOnScrollCalls = 74;
        int expectedMaxOnScrollCalls = 77;

        objectUnderTest.onFling(e1, e2, velocityX, velocityY);

        synchronized (objectUnderTest) {
            try {
                objectUnderTest.wait(expectedMaxDurationMilliSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockBibleCast, Mockito.atLeast(expectedMinOnScrollCalls)).sendMessage(messageCaptor.capture());
        Mockito.verify(mockBibleCast, Mockito.atMost(expectedMaxOnScrollCalls)).sendMessage(messageCaptor.capture());

        for (String message : messageCaptor.getAllValues()) {
            Log.d(TAG, message);
        }
    }
}