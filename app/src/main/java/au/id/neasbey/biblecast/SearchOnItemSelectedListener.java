package au.id.neasbey.biblecast;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import au.id.neasbey.biblecast.model.BibleVersion;

/**
 * Created by craigneasbey on 1/10/15.
 */
public class SearchOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    private static final String TAG = SearchOnItemSelectedListener.class.getSimpleName();

    private BibleSearch bibleSearch;

    public SearchOnItemSelectedListener(BibleSearch bibleSearch) {
        this.bibleSearch = bibleSearch;
    }

    @Override
    public void onItemSelected (AdapterView<?> parent, View view,int position, long id) {
        bibleSearch.setBibleVersion(((BibleVersion) parent.getItemAtPosition(position)).getId());
        Log.d(TAG, "BibleVersion: " + bibleSearch.getBibleVersion());
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent) {
        int selected = -1;

        List<BibleVersion> versionList = bibleSearch.getVersions();

        for (int i = 0; i < versionList.size(); i++) {
            BibleVersion version = versionList.get(i);
            if (version.getId().equalsIgnoreCase(bibleSearch.getBibleVersion())) {
                selected = i;
            }
        }

        if(selected >= 0) {
            parent.setSelection(selected);
        } else if ( versionList.size() > 0) {
            bibleSearch.setBibleVersion(versionList.get(0).getId());
            Log.d(TAG, "BibleVersion: " + bibleSearch.getBibleVersion());
        }
    }
}
