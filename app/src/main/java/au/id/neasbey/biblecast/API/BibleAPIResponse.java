package au.id.neasbey.biblecast.API;

/**
 * Created by craigneasbey on 26/08/15.
 *
 * Holds the Bible API response
 */
public class BibleAPIResponse {

    private static final String TAG = BibleAPIResponse.class.getSimpleName();

    public static final int responseCodeOk = 200;

    public static final String responseMessageOk = "OK";

    public static final int responseCodeNotFound = 404;

    public static final String responseMessageNotFound = "Not Found";

    public static final int responseCodeUnauthorized = 401;

    public static final String responseMessageUnauthorized = "Unauthorized";

    private int responseCode;

    private String responseMessage;

    private String responseData;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}
