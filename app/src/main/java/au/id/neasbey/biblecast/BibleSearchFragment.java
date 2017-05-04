package au.id.neasbey.biblecast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by craigneasbey on 30/06/15.
 *
 * A fragment of the Bible Search activity
 */
public class BibleSearchFragment extends Fragment {

    public BibleSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bible_search, container, false);
    }

    // TODO BIBLECAST#6 - move to fragment
}
