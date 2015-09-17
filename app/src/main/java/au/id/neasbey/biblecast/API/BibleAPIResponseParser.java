package au.id.neasbey.biblecast.API;

import android.text.Spanned;

import java.util.List;

import au.id.neasbey.biblecast.BibleVersion;

/**
 * Created by craigneasbey on 30/06/15.
 *
 * Bible API response parser utility
 */
public abstract class BibleAPIResponseParser {

    /**
     * Parse the response status
     *
     * @param responseCode HTTP response status code
     * @param responseMessage HTTP response status message
     * @throws BibleSearchAPIException
     */
    public abstract void parseResponseStatus(int responseCode, String responseMessage) throws BibleSearchAPIException;

    /**
     * Parse the response string
     *
     * @param responseString Response string to parse
     * @return Resulting list after parsing the string
     * @throws BibleSearchAPIException
     */
    public abstract List<Spanned> parseResponseDataToSpannedList(String responseString) throws BibleSearchAPIException;

    public abstract List<BibleVersion> parseResponseDataToVersionList(String responseString) throws BibleSearchAPIException;

    public abstract List<String> parseResponseDataToStringList(String responseString) throws BibleSearchAPIException;
}
