package au.id.neasbey.biblecast.GUIHelper;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by craigneasbey on 16/09/15.
 * Derived from http://examples.javacodegeeks.com/android/core/content/contentprovider/android-content-provider-example/
 *
 * Stories bible book suggestions for the search query
 */
public class SearchSuggestionProvider extends ContentProvider {

    private static final String TAG = SearchSuggestionProvider.class.getSimpleName();

    public static final String PROVIDER_NAME = "au.id.neasbey.biblecast.SearchSuggestion";
    public static final String URL = "content://" + PROVIDER_NAME + "/suggestions";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    // database fields
    public static final String ID = BaseColumns._ID;
    public static final String SUGGESTION = SearchManager.SUGGEST_COLUMN_TEXT_1;

    // integer values used in content URI
    public static final int SUGGESTIONS = 1;
    public static final int SUGGESTIONS_ID = 2;
    public static final int SUGGESTIONS_SEARCH = 3;

    // maps content URI "patterns" to the integer values that were set above
    public static final UriMatcher uriMatcher;
    public static final String DATABASE_NAME = "Biblecast";
    public static final String TABLE_NAME = "SearchSuggestions";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (" + ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + SUGGESTION + " TEXT NOT NULL);";

    // projection map for a query
    private static HashMap<String, String> suggestionMap = buildColumnMap();

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "suggestions", SUGGESTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "suggestions/#", SUGGESTIONS_ID);
        uriMatcher.addURI(PROVIDER_NAME, SearchManager.SUGGEST_URI_PATH_QUERY + "/", SUGGESTIONS_SEARCH);
        uriMatcher.addURI(PROVIDER_NAME, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SUGGESTIONS_SEARCH);
    }

    DBHelper dbHelper;
    // database declarations
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);

        // permissions to be writable
        database = dbHelper.getWritableDatabase();

        if (database == null) {
            return false;
        } else {
            return true;
        }
    }

    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SUGGESTION, SUGGESTION);
        return map;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // the TABLE_NAME to query on
        queryBuilder.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            // maps all database column names
            case SUGGESTIONS:
                queryBuilder.setProjectionMap(suggestionMap);
                break;
            case SUGGESTIONS_ID:
                queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
                break;
            case SUGGESTIONS_SEARCH:
                queryBuilder.appendWhere("lower(" + SUGGESTION + ") LIKE '%" + uri.getLastPathSegment().toLowerCase() + "%'");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == "") {
            // No sorting-> sort on suggestions by default
            sortOrder = SUGGESTION;
        }

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = database.insert(TABLE_NAME, "", values);
        Log.d(TAG, "Add row: " + values.toString());

        // If record is added successfully
        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Failed to add a new record into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case SUGGESTIONS:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case SUGGESTIONS_ID:
                String id = uri.getLastPathSegment();
                count = database.update(TABLE_NAME, values, ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case SUGGESTIONS:
                // delete all the records of the table
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case SUGGESTIONS_ID:
                count = database.delete(TABLE_NAME, ID + " = " + uri.getLastPathSegment() + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;


    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            // Get all suggestions
            case SUGGESTIONS:
                return "vnd.android.cursor.dir/vnd.au.id.neasbey.biblecast.GUIHelper.SearchSuggestionProvider.suggestions";
            // Get a suggestion
            case SUGGESTIONS_ID:
                return "vnd.android.cursor.item/vnd.au.id.neasbey.biblecast.GUIHelper.SearchSuggestionProvider.suggestions";
            // Get suggestions that match the query
            case SUGGESTIONS_SEARCH:
                return "vnd.android.cursor.item/vnd.au.id.neasbey.biblecast.GUIHelper.SearchSuggestionProvider.suggestions";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    // class that creates and manages the provider's database
    private static class DBHelper extends SQLiteOpenHelper {

        private static final String TAG = DBHelper.class.getSimpleName();

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ". Old data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

    }
}
