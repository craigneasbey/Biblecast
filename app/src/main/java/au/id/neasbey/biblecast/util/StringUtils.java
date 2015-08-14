package au.id.neasbey.biblecast.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import au.id.neasbey.biblecast.API.BibleSearchAPIException;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Helpful String utilities
 */
public class StringUtils {

    public static final String UTF_8 = "UTF-8";

    /**
     * URL Encodes a map using the character set UTF-8
     *
     * @param map Key/Vale strings to encode
     * @return Encoded string
     * @throws BibleSearchAPIException
     */
    public static String urlEncodeUTF8(Map<?, ?> map) throws BibleSearchAPIException {
        StringBuilder sb = new StringBuilder();

        try {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }

                sb.append(urlEncodeUTF8(entry.getKey().toString()));
                sb.append("=");
                sb.append(urlEncodeUTF8(entry.getValue().toString()));
            }
        } catch (UnsupportedOperationException uoe) {
            throw new BibleSearchAPIException("URL parameter encoding error: " + uoe.getMessage());
        }

        return sb.toString();
    }

    /**
     * URL Encodes a String using the character set UTF-8
     *
     * @param s String to encode
     * @return Encoded string
     * @throws UnsupportedOperationException
     */
    public static String urlEncodeUTF8(String s) throws UnsupportedOperationException {
        try {
            return URLEncoder.encode(s, StringUtils.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Base64 encodes a string for HTTP authentication
     *
     * @param bytes Authentication string
     * @return Encoded property string
     */
    public static String generateHttpAuthentication(byte[] bytes) {
        return "Basic " + new String(android.util.Base64.encode(bytes, android.util.Base64.NO_WRAP));
    }
}
