package au.id.neasbey.biblecast.API.BiblesOrg;

import android.text.TextUtils;
import android.util.Log;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import au.id.neasbey.biblecast.API.BibleAPIConnectionHandler;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Handles the HTTP connection to the Bibles.org Bible API
 */
public class BibleAPIConnectionHandlerBiblesOrg extends BibleAPIConnectionHandler {

    private static final String TAG = BibleAPIConnectionHandlerBiblesOrg.class.getSimpleName();

    /**
     * Sets the default authenticator for HTTP connections
     *
     * @throws BibleSearchAPIException
     */
    @Override
    protected void authenticate() throws BibleSearchAPIException {

        if (TextUtils.isEmpty(getUsername())) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_no_auth));
        }

        // Password is not required for Bibles.org API
        if (TextUtils.isEmpty(getPassword())) {
            setPassword("");
        }

        // Set API authentication
        Authenticator.setDefault(new BasicAuthenticator(getUsername(), getPassword()));
    }

    private static class BasicAuthenticator extends Authenticator {
        private String name;
        private String password;

        private BasicAuthenticator(String name, String password) {
            this.name = name;
            this.password = password;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            Log.d(TAG, "Authenticating Bible API...");
            return new PasswordAuthentication(name, password.toCharArray());
        }
    };
}
