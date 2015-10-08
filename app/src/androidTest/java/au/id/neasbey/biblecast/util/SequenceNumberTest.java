package au.id.neasbey.biblecast.util;

import junit.framework.TestCase;

/**
 * Created by craigneasbey on 9/09/15.
 */
public class SequenceNumberTest extends TestCase {

    public void testGet() throws Exception {
        SequenceNumber sq = new SequenceNumber();

        assertEquals(0, sq.get());
        assertEquals(1, sq.get());
        assertEquals(2, sq.get());
        assertEquals(3, sq.get());
    }
}