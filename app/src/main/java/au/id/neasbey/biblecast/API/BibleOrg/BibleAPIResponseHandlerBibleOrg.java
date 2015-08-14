package au.id.neasbey.biblecast.API.BibleOrg;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Handles the the response from the Bible.org Bible API
 */
public class BibleAPIResponseHandlerBibleOrg extends BibleAPIResponseHandler {

    private static final String TAG = BibleAPIResponseHandlerBibleOrg.class.getSimpleName();

    public BibleAPIResponseHandlerBibleOrg(BibleAPIConnectionHandler bibleAPIConnectionHandler, BibleAPIResponseParser bibleAPIResponseParser) {
        super(bibleAPIConnectionHandler, bibleAPIResponseParser);
    }

    /**
     * Checks the response code from the Bible.org Bible API server
     * @throws BibleSearchAPIException
     */
    @Override
    protected void checkServerResponse() throws BibleSearchAPIException {
        if (getResponseCode() == 401) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_bible_org_incorrect));
        }

        if (getResponseCode() != 200) {
            throw new BibleSearchAPIException(getResponseCode() + " - " + getResponseMessage());
        }
    }
}
