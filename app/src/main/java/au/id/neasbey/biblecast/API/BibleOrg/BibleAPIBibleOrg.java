package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.UIUtils;
import au.id.neasbey.biblecast.util.URLWrapper;
import au.id.neasbey.biblecast.util.StringUtils;

/**
 * Created by craigneasbey on 11/08/15.
 * <p/>
 * Search using the Bible.org Bible API
 */
public class BibleAPIBibleOrg extends BibleAPI {

    private static final String TAG = BibleAPIBibleOrg.class.getSimpleName();

    private static final String queryParameter = "query";

    private static final String versionParameter = "version";

    /**
     * Checks the API parameters, then encodes them
     *
     * @return Encoded parameters for the API URL
     * @throws BibleSearchAPIException
     */
    @Override
    public String getRequestParameters() throws BibleSearchAPIException {

        if (TextUtils.isEmpty(getQuery())) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_no_query));
        }

        if (TextUtils.isEmpty(getVersions())) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_no_version));
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put(queryParameter, getQuery());
        parameters.put(versionParameter, getVersions());

        return "?" + StringUtils.urlEncodeUTF8(parameters);
    }
}
