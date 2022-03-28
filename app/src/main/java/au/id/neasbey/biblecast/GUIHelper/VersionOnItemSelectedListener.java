package au.id.neasbey.biblecast.GUIHelper;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import au.id.neasbey.biblecast.BibleSearch;
import au.id.neasbey.biblecast.model.BibleVersion;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 1/10/15.
 *
 * Listens to the bible version spinner for item selection and sets the bible version
 */
public class VersionOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    private static final String TAG = VersionOnItemSelectedListener.class.getSimpleName();

    private BibleSearch bibleSearch;

    public VersionOnItemSelectedListener(BibleSearch bibleSearch) {
        this.bibleSearch = bibleSearch;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        bibleSearch.setBibleVersion(((BibleVersion) parent.getItemAtPosition(position)).getId());
        Log.d(TAG, "BibleVersion: " + bibleSearch.getBibleVersion());

        bibleSearch.performSearch();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        int selected = -1;
        List<BibleVersion> versionList = bibleSearch.getVersions();

        selected = versionList.indexOf(UIUtils.findBibleVersionById(versionList, bibleSearch.getBibleVersion()));

        if (selected >= 0) {
            // if the current bible version is in the list, select it
            parent.setSelection(selected);
        } else if (versionList.size() > 0) {
            // if the bible version is not in the list, select the first
            selected = 0;
            parent.setSelection(selected);
            bibleSearch.setBibleVersion(versionList.get(selected).getId());
            Log.d(TAG, "BibleVersion: " + bibleSearch.getBibleVersion());
        }
    }
}
