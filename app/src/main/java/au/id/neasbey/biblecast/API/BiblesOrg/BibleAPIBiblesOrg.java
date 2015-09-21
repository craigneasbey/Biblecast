package au.id.neasbey.biblecast.API.BiblesOrg;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import au.id.neasbey.biblecast.API.BibleAPI;
import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.BiblecastException;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.HttpUtils;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 11/08/15.
 * <p/>
 * Search using the Bibles.org Bible API
 */
public class BibleAPIBiblesOrg extends BibleAPI {

    private static final String TAG = BibleAPIBiblesOrg.class.getSimpleName();

    private static final String queryParameter = "query";

    private static final String versionParameter = "version";

    private static final String languageParameter = "language";

    public BibleAPIBiblesOrg(BibleAPIConnectionHandler bibleAPIConnectionHandler, BibleAPIResponseParser bibleAPIResponseParser) {
        super(bibleAPIConnectionHandler,  bibleAPIResponseParser);
    }

    @Override
    public String query() {

        // pass authentication to connection handler
        getBibleAPIConnectionHandler().setUsername(getUsername());
        getBibleAPIConnectionHandler().setPassword(getPassword());

        return super.query();
    }

    /**
     * Checks the API parameters, then encodes them
     *
     * @return Encoded parameters for the API URL
     * @throws BiblecastException
     */
    @Override
    public String getRequestParameters() throws BiblecastException {

        switch (getQueryType()) {
            case SEARCH:
                if (TextUtils.isEmpty(getQuery())) {
                    throw new BiblecastException(UIUtils.getContext().getString(R.string.api_no_query));
                }

                if (TextUtils.isEmpty(getVersions())) {
                    throw new BiblecastException(UIUtils.getContext().getString(R.string.api_no_version));
                }

                Map<String, String> searchParameters = new HashMap<>();
                searchParameters.put(queryParameter, getQuery());
                searchParameters.put(versionParameter, getVersions());

                return "?" + HttpUtils.urlEncodeUTF8(searchParameters);
            case VERSION:
                if (TextUtils.isEmpty(getLanguage())) {
                    throw new BiblecastException(UIUtils.getContext().getString(R.string.api_no_language));
                }

                Map<String, String> versionParameters = new HashMap<>();
                versionParameters.put(languageParameter, getLanguage());

                return "?" + HttpUtils.urlEncodeUTF8(versionParameters);
            default:
                return "";
        }
    }
}
