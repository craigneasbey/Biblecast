package au.id.neasbey.biblecast.util;

import com.google.android.gms.cast.CastDevice;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.mockito.Mockito;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Date;

import au.id.neasbey.biblecast.exception.BiblecastException;
import au.id.neasbey.biblecast.model.Dimensions;

/**
 * Created by craigneasbey on 21/09/15.
 *
 * Tests google cast utilities
 */
public class CastUtilsTest extends TestCase {

    public void testParseMessageForDimensionsSuccessful() throws Exception {
        final String testJSON = "{ \"dimensions\" : { \"width\" : 2, \"height\" : 34 } }";
        final Dimensions expected = new Dimensions(2, 34);

        Dimensions actual = CastUtils.parseMessageForDimensions(testJSON);

        assertEquals(expected, actual);
    }

    public void testParseMessageForDimensionsFails() throws Exception {
        final String testJSON = "{ \"dimensions\" : { \"width\" : 2, \"height\" : 34 } }";
        final Dimensions expected = new Dimensions(2, 35);

        Dimensions actual = CastUtils.parseMessageForDimensions(testJSON);

        assertFalse(expected.equals(actual));
    }

    public void testParseMessageForDelaySuccessful() throws Exception {
        final long now = new Date().getTime();
        final long sendMS = now - 143;
        final long receiveMS = now - 112;

        final String testJSON = "{ \"delay\" : { \"send\" : " + sendMS + ", \"receive\" : " + receiveMS + " } }";
        final String expected = "Delay - Send: 31ms Receive: 112ms Trip: 143ms";

        String actual = CastUtils.parseMessageForDelay(testJSON, now);

        assertEquals(expected, actual);
    }

    public void testParseMessageForDelayFails() throws Exception {
        final String testJSON = "{\"test\"}";
        boolean exceptions = false;

        try {
            CastUtils.parseMessageForDelay(testJSON);
        } catch(BiblecastException bce) {
            exceptions = true;
            assertEquals("JSON does not contain delay data", bce.getMessage());
        };

        assertEquals(true, exceptions);
    }

    public void testAddDelay() throws Exception {
        final String testJSON = "{\"test\":\"data\"}";
        final long now = new Date().getTime();
        final String expectedJSON = "{\"test\":\"data\",\"delay\":{\"send\":" + now + "}}";

        String actualJSON = CastUtils.addDelay(testJSON, now);

        assertEquals(expectedJSON, actualJSON);
    }

    public void testDeviceInformation() throws Exception {
        final String expected = "Device ID: DeviceID Version: Version Name: Name IP: localhost/127.0.0.1 Model: Model Port: 9222 Status: 1 Local: true Contents: 1 Capabilities: 1";

        CastDevice castDevice = Mockito.mock(CastDevice.class);

        Mockito.when(castDevice.getDeviceId()).thenReturn("DeviceID");
        Mockito.when(castDevice.getDeviceVersion()).thenReturn("Version");
        Mockito.when(castDevice.getFriendlyName()).thenReturn("Name");
        Mockito.when(castDevice.getIpAddress()).thenReturn((Inet4Address)InetAddress.getByName("localhost"));
        Mockito.when(castDevice.getModelName()).thenReturn("Model");
        Mockito.when(castDevice.getServicePort()).thenReturn(9222);
        Mockito.when(castDevice.getStatus()).thenReturn(1);
        Mockito.when(castDevice.isOnLocalNetwork()).thenReturn(true);
        Mockito.when(castDevice.describeContents()).thenReturn(1);
        Mockito.when(castDevice.getCapabilities()).thenReturn(1);

        String actual = CastUtils.getDeviceInfo(castDevice);

        assertEquals(expected, actual);
    }
}