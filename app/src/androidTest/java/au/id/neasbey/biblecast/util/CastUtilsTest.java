package au.id.neasbey.biblecast.util;

import junit.framework.TestCase;

import au.id.neasbey.biblecast.model.Dimensions;

/**
 * Created by craigneasbey on 21/09/15.
 *
 * Tests google cast utilities
 */
public class CastUtilsTest extends TestCase {

    public void testParseMessageForDimensionsSuccessful() throws Exception {
        String testJSON = "{ \"dimensions\" : { \"width\" : \"2\", \"height\" : \"34\" } }";
        Dimensions expected = new Dimensions(2, 34);

        Dimensions actual = CastUtils.parseMessageForDimensions(testJSON);

        assertEquals(expected, actual);
    }

    public void testParseMessageForDimensionsFails() throws Exception {
        String testJSON = "{ \"dimensions\" : { \"width\" : \"2\", \"height\" : \"34\" } }";
        Dimensions expected = new Dimensions(2, 35);

        Dimensions actual = CastUtils.parseMessageForDimensions(testJSON);

        assertFalse(expected.equals(actual));
    }
}