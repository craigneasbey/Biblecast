package au.id.neasbey.biblecast.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by craigneasbey on 1/07/15.
 *
 * Helpful user interface utilities
 */
public class UIUtils {

    private static Context context = null;

    /**
     * Allows configuration to be accessed across the application
     *
     * eg. {@code UIUtils.getContext().getString(R.string.api_no_url)}
     *
     * @param context Application context
     */
    public static void setContext(Context context)
    {
        UIUtils.context = context;
    }

    public static Context getContext() {
        return UIUtils.context;
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
}
