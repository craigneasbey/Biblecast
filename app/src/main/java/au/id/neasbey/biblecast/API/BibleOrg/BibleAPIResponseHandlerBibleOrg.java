package au.id.neasbey.biblecast.API.BibleOrg;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseHandler;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;

/**
 * Created by craigneasbey on 11/08/15.
 */
public class BibleAPIResponseHandlerBibleOrg extends BibleAPIResponseHandler {

    private static final String TAG = BibleAPIResponseHandlerBibleOrg.class.getSimpleName();

    public BibleAPIResponseHandlerBibleOrg(BibleAPIConnectionHandler bibleAPIConnectionHandler, BibleAPIResponseParser bibleAPIResponseParser) {
        super(bibleAPIConnectionHandler, bibleAPIResponseParser);
    }

    @Override
    protected void checkServerResponse() throws BibleSearchAPIException {
        if (getResponseCode() == 401) {
            throw new BibleSearchAPIException("Application API token is incorrect");
        }

        if (getResponseCode() != 200) {
            throw new BibleSearchAPIException(getResponseCode() + " - " + getResponseMessage());
        }
    }
}
