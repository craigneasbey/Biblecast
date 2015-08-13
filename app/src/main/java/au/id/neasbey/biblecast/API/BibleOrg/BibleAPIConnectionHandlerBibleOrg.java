package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.TextUtils;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.util.StringUtils;

/**
 * Created by craigneasbey on 11/08/15.
 */
public class BibleAPIConnectionHandlerBibleOrg extends BibleAPIConnectionHandler {

    @Override
    protected void authenticate() throws BibleSearchAPIException {

        if (TextUtils.isEmpty(getAuth())) {
            throw new BibleSearchAPIException("No API authentication specified");
        }

        // Set API authentication
        getHttpUrlConnection().addRequestProperty("Authorization", StringUtils.generateHttpAuthentication(getAuth().getBytes()));
    }
}
