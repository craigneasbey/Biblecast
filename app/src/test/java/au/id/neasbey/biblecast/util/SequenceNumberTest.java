package au.id.neasbey.biblecast.util;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import au.id.neasbey.biblecast.BuildConfig;

import static org.junit.Assert.assertEquals;

/**
 * Created by craigneasbey on 9/09/15.
 *
 * Tests for a unique sequence number
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SequenceNumberTest {

    @BeforeClass
    public static void setupRobolectric() {
        ShadowLog.stream = System.out;
    }

    @Test
    public void shouldGetNextSequenceNumber() throws Exception {
        SequenceNumber sq = new SequenceNumber();

        assertEquals(0, sq.get());
        assertEquals(1, sq.get());
        assertEquals(2, sq.get());
        assertEquals(3, sq.get());
    }
}