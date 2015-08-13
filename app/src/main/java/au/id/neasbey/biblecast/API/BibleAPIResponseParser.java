package au.id.neasbey.biblecast.API;

import android.text.Spanned;

import java.util.List;

/**
 * Created by craigneasbey on 30/06/15.
 *
 * Bible API response parser utility
 */
public abstract class BibleAPIResponseParser {

    /**
     * Parse the response string
     *
     * @param responseString
     * @param resultList
     * @throws BibleSearchAPIException
     */
    public abstract void parseResponseToList(String responseString, List<Spanned> resultList) throws BibleSearchAPIException;
}
