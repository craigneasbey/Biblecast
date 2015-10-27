package au.id.neasbey.biblecast.util;

import android.text.Html;
import android.text.Spanned;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import au.id.neasbey.biblecast.exception.BiblecastException;

/**
 * Created by craigneasbey on 11/08/15.
 * <p/>
 * Helpful String utilities
 */
public class HttpUtils {

    public static final String UTF_8 = "UTF-8";

    public static final int MAX_WORD_GROUP = 20;

    public static String SPACE = " ";

    /**
     * URL Encodes a map using the character set UTF-8
     *
     * @param map Key/Vale strings to encode
     * @return Encoded string
     * @throws BiblecastException
     */
    public static String urlEncodeUTF8(Map<?, ?> map) throws BiblecastException {
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
            throw new BiblecastException("URL parameter encoding error: " + uoe.getMessage());
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
            return URLEncoder.encode(s, HttpUtils.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Adds anchors to a sentence for scrolling
     *
     * @param sentence
     * @param sq
     * @return String with HTML anchors between each sentence
     */
    public static String addAnchors(String sentence, SequenceNumber sq) {

        boolean startTag;
        StringBuilder groupedSentence = new StringBuilder();

        String[] words = sentence.split(SPACE);

        int i = 0;
        while (i < words.length) {
            startTag = true;

            for (int j = 0; j < MAX_WORD_GROUP && i < words.length; j++) {
                String word = words[i];

                if (startTag) {
                    //groupedSentence.append("<span id=\"" + sq.get() + "\">");
                    //groupedSentence.append("<span id=\"" + sq.get() + "\">");
                    startTag = false;
                } else {
                    groupedSentence.append(SPACE);
                }

                groupedSentence.append(word);
                i++;
            }

            /*if (!startTag) {
                groupedSentence.append("</span>");
            }*/
        }

        return groupedSentence.toString();
    }

    /**
     * Converts spanned list to JSON
     *
     * @param elementsList HTML list
     * @return JSON string
     */
    public static String listToJSON(List<Spanned> elementsList) {

        StringBuilder sb = new StringBuilder();

        if (elementsList != null && !elementsList.isEmpty()) {
            boolean first = true;

            sb.append("{ \"elements\" : [ \"");

            for (Spanned element : elementsList) {
                if (first) {
                    first = false;
                } else {
                    sb.append("\", \"");
                }

                sb.append(spannedToJSON(element));
            }

            sb.append("\" ] }");
        }

        return sb.toString();
    }

    public static String spannedToJSON(Spanned element) {
        if(element != null) {
            return Html.toHtml(element).trim().replace("\"", "\\\"");
        }

        return "";
    }
}
