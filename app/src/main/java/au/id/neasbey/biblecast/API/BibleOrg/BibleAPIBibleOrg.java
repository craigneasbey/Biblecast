package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.util.URLWrapper;
import au.id.neasbey.biblecast.util.StringUtils;

/**
 * Created by craigneasbey on 11/08/15.
 * <p/>
 * Test the Bible API for Bible.org
 */
public class BibleAPIBibleOrg extends BibleAPI {

    private static final String TAG = BibleAPIBibleOrg.class.getSimpleName();

    @Override
    public String getRequestParameters() throws BibleSearchAPIException {

        if (TextUtils.isEmpty(getQuery())) {
            throw new BibleSearchAPIException("No API query specified");
        }

        if (TextUtils.isEmpty(getVersions())) {
            throw new BibleSearchAPIException("No API bible versions specified");
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("query", getQuery());
        parameters.put("version", getVersions());

        return "?" + StringUtils.urlEncodeUTF8(parameters);
    }
}
