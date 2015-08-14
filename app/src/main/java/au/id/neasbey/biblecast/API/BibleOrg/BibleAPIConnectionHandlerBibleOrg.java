package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.TextUtils;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.StringUtils;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Handles the HTTP connection to the Bible.org Bible API
 */
public class BibleAPIConnectionHandlerBibleOrg extends BibleAPIConnectionHandler {

    private static final String authenticationKey = "Authorization";

    /**
     * Adds authentication property to the HTTP connection after the connection is open
     *
     * @throws BibleSearchAPIException
     */
    @Override
    protected void authenticate() throws BibleSearchAPIException {

        if (TextUtils.isEmpty(getAuth())) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_no_auth));
        }

        // Set API authentication
        getHttpUrlConnection().addRequestProperty(authenticationKey, StringUtils.generateHttpAuthentication(getAuth().getBytes()));
    }
}
