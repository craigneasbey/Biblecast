package au.id.neasbey.biblecast.API;

/**
 * Created by craigneasbey on 26/08/15.
 *
 * Holds the Bible API response
 */
public class BibleAPIResponse {

    private static final String TAG = BibleAPIResponse.class.getSimpleName();

    public static final int RESPONSE_CODE_OK = 200;

    public static final String RESPONSE_MESSAGE_OK = "OK";

    public static final int RESPONSE_CODE_NOT_FOUND = 404;

    public static final String RESPONSE_MESSAGE_NOT_FOUND = "Not Found";

    public static final int RESPONSE_CODE_UNAUTHORIZED = 401;

    public static final String RESPONSE_MESSAGE_UNAUTHORIZED = "Unauthorized";

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
