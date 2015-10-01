package au.id.neasbey.biblecast.GUIHelper;

import android.database.Cursor;
import android.widget.SearchView;

/**
 * Created by craigneasbey on 1/10/15.
 */
public class SearchOnSuggestionListener implements SearchView.OnSuggestionListener {

    private SearchView searchView;

    public SearchOnSuggestionListener(SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return updateSearchQuery(position);
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return updateSearchQuery(position);
    }

    public boolean updateSearchQuery(int position) {
        // http://ramannanda.blogspot.com.au/2014/10/android-searchview-integration-with.html
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        String feedName = cursor.getString(cursor.getColumnIndex(SearchSuggestionProvider.SUGGESTION));
        searchView.setQuery(feedName, false);

        return true;
    }
}
