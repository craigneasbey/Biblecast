package au.id.neasbey.biblecast.GUIHelper;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by craigneasbey on 1/10/15.
 */
public class SuggestionAsyncQueryHandler extends AsyncQueryHandler {

    private static final String TAG = SuggestionAsyncQueryHandler.class.getSimpleName();

    public SuggestionAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete (int token, Object cookie, Cursor cursor) {
        List<String> currentSuggestions = new LinkedList<>();

        // add current suggestions to memory
        while(cursor != null && cursor.moveToNext()) {
            currentSuggestions.add(cursor.getString(cursor.getColumnIndex(SearchSuggestionProvider.SUGGESTION)));
        }

        updateSuggestions(currentSuggestions, ((List<String>) cookie));
    }

    private void updateSuggestions(List<String> currentSuggestions, List<String> newSuggestions) {

        // check all current suggestions for missing new suggestions, if found add
        boolean found;

        for (String newSuggestion : newSuggestions) {
            found = false;
            for (String currentSuggestion : currentSuggestions) {
                if (currentSuggestion.equalsIgnoreCase(newSuggestion)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                addSuggestion(newSuggestion);
            }
        }

        // check all new suggestions for missing current suggestions, if found remove
        for (String currentSuggestion : currentSuggestions) {
            found = false;
            for (String newSuggestion : newSuggestions) {
                if (currentSuggestion.equalsIgnoreCase(newSuggestion)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                removeSuggestion(currentSuggestion);
            }
        }
    }

    public void removeAllSuggestions() {
        Log.d(TAG, "Remove all suggestions");
        startDelete(0, null, SearchSuggestionProvider.CONTENT_URI, null, null);
    }

    public void removeSuggestion(String suggestion) {
        Log.d(TAG, "Remove suggestion: " + suggestion);
        startDelete(0, null, SearchSuggestionProvider.CONTENT_URI, suggestion, null);
    }

    public void addSuggestion(String suggestion) {
        Log.d(TAG, "Add suggestion: " + suggestion);

        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchSuggestionProvider.SUGGESTION, suggestion);

        startInsert(0, null, SearchSuggestionProvider.CONTENT_URI, contentValues);
    }
}