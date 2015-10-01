package au.id.neasbey.biblecast.util;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import au.id.neasbey.biblecast.exception.BiblecastException;
import au.id.neasbey.biblecast.model.Dimensions;

/**
 * Created by craigneasbey on 21/09/15.
 *
 * Google Cast utilities
 */
public class CastUtils {

    private static final String dimensionsKey = "dimensions";

    private static final String heightKey = "height";

    private static final String widthKey = "width";

    public static Dimensions parseMessageForDimensions(String jsonMessage) throws BiblecastException {

        int width = 0;
        int height = 0;

        if(!TextUtils.isEmpty(jsonMessage)) {

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
}
