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
