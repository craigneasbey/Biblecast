package au.id.neasbey.biblecast.util;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;

import au.id.neasbey.biblecast.model.BibleVersion;

/**
 * Created by craigneasbey on 1/07/15.
 * <p/>
 * Helpful user interface utilities
 */
public class UIUtils {

    private static Context context = null;

    public static Context getContext() {
        return UIUtils.context;
    }

    /**
     * Allows configuration to be accessed across the application
     * <p/>
     * eg. {@code UIUtils.getContext().getString(R.string.api_no_url)}
     *
     * @param context Application context
     */
    public static void setContext(Context context) {
        UIUtils.context = context;
    }

    /**
     * Displays an Error Dialog prompt
     *
     * @param context Context to display prompt over
     * @param title   Title of error
     * @param button  OK button text
     * @param message Error message
     */
    public static void displayError(Context context, int title, String button, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Add the OK button
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        builder.setTitle(title).setMessage(message);
        builder.create().show();
    }

    /**
     * Finds a BibleVersion from a list by ID
     *
     * @param versionList List of BibleVersions
     * @param versionId   ID of the BibleVersion to find
     * @return Found BibleVersion or null if not found
     */
    public static BibleVersion findBibleVersionById(List<BibleVersion> versionList, String versionId) {

        for (BibleVersion bibleVersion : versionList) {
            if (bibleVersion.getId().equalsIgnoreCase(versionId)) {
                return bibleVersion;
            }
        }

        return null;
    }
}
