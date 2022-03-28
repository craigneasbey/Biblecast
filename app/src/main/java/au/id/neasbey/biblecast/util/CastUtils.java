package au.id.neasbey.biblecast.util;

import android.text.TextUtils;

import com.google.android.gms.cast.CastDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import au.id.neasbey.biblecast.exception.BiblecastException;
import au.id.neasbey.biblecast.model.Dimensions;

/**
 * Created by craigneasbey on 21/09/15.
 * <p/>
 * Google Cast utilities
 */
public class CastUtils {

    private static final String dimensionsKey = "dimensions";

    private static final String heightKey = "height";

    private static final String widthKey = "width";

    private static final String delayKey = "delay";

    private static final String sendKey = "send";

    private static final String receiveKey = "receive";

    public static Dimensions parseMessageForDimensions(String jsonMessage) throws BiblecastException {

        int width = 0;
        int height = 0;

        if (!TextUtils.isEmpty(jsonMessage)) {

            // Creates a new JSONObject with name/value mappings from the JSON string
            try {
                JSONObject jsonValues = new JSONObject(jsonMessage);

                // Get dimension values
                JSONObject dimensionsValues = jsonValues.getJSONObject(dimensionsKey);
                width = dimensionsValues.optInt(widthKey, width);
                height = dimensionsValues.optInt(heightKey, height);

            } catch (JSONException e) {
                throw new BiblecastException("JSON does not contain dimension data");
            }
        }

        return new Dimensions(width, height);
    }

    public static String parseMessageForDelay(String message) throws BiblecastException {
        return parseMessageForDelay(message, new Date().getTime());
    }

    public static String parseMessageForDelay(String message, long currentMS) throws BiblecastException {
        StringBuilder result = new StringBuilder();

        if (!TextUtils.isEmpty(message)) {

            // Creates a new JSONObject with name/value mappings from the JSON string
            try {
                JSONObject jsonValues = new JSONObject(message);

                // Get delay values
                JSONObject dimensionsValues = jsonValues.getJSONObject(delayKey);
                long sendMS = dimensionsValues.getLong(sendKey);
                long receiveMS = dimensionsValues.optLong(receiveKey);

                result.append("Delay - Send: ");
                result.append(calculateDelayMS(sendMS, receiveMS));
                result.append("ms Receive: ");
                result.append(calculateDelayMS(receiveMS, currentMS));
                result.append("ms Trip: ");
                result.append(calculateDelayMS(sendMS, currentMS));
                result.append("ms");
            } catch (JSONException e) {
                throw new BiblecastException("JSON does not contain delay data");
            }
        }

        return result.toString();
    }

    public static long calculateDelayMS(long first, long second) {
        return second - first;
    }

    /**
     * Add delay to cast message
     * @param jsonMessage JSON being send to cast
     * @return Cast message with sender time in milliseconds
     */
    public static String addDelay(String jsonMessage) {
        return addDelay(jsonMessage, new Date().getTime());
    }


    /**
     * Add delay to cast message
     * @param jsonMessage JSON being send to cast
     * @param sendMS Send time in milliseconds
     * @return Cast message with sender time in milliseconds
     */
   public static String addDelay(String jsonMessage, long sendMS) {

        try {
            JSONObject jsonDelay = new JSONObject();
            jsonDelay.put("send", sendMS);

            JSONObject jsonValues = new JSONObject(jsonMessage);
            jsonValues.put("delay", jsonDelay);
            jsonMessage = jsonValues.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonMessage;
    }

    /**
     * Google Cast device information
     * @param device The receiver cast device
     * @return Device information
     */
    public static String getDeviceInfo(CastDevice device) {
        StringBuilder sb = new StringBuilder();

        if(device != null) {
            sb.append("Device ID: ");
            sb.append(device.getDeviceId());
            sb.append(" Version: ");
            sb.append(device.getDeviceVersion());
            sb.append(" Name: ");
            sb.append(device.getFriendlyName());
            sb.append(" IP: ");
            sb.append(device.getIpAddress().toString());
            sb.append(" Model: ");
            sb.append(device.getModelName());
            sb.append(" Port: ");
            sb.append(device.getServicePort());
            sb.append(" Status: ");
            sb.append(device.getStatus());
            sb.append(" Local: ");
            sb.append(device.isOnLocalNetwork());
            sb.append(" Contents: ");
            sb.append(device.describeContents());
            sb.append(" Capabilities: ");
            sb.append(device.getCapabilities());
        }

        return sb.toString();
    }
}
