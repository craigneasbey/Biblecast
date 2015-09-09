package au.id.neasbey.biblecast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by craigneasbey on 30/06/15.
 *
 * A fragment of the Bible Search activity
 */
public class BibleSearchFragment extends Fragment {

    private static final String TAG = BibleSearchFragment.class.getSimpleName();

    private ListView listView;

    private float ratio;

    private boolean ratioSet;

    private boolean ratioApplied;

    private int resultsInitialHeight;

    private int resultsInitialWidth;

    private boolean initialised;

    public BibleSearchFragment() {
        ratio = 1;
        ratioSet = false;
        ratioApplied = false;
        resultsInitialHeight = 0;
        resultsInitialWidth = 0;
        initialised = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_bible_search, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    /**
     * Resets to original results list view dimensions if the ratio has been
     * applied, otherwise, sets the original results list view dimensions.
     */
    public void resetResultsListView() {

        if(listView == null) {
            listView = (ListView) getActivity().findViewById(R.id.listView);
        }

        if(ratioApplied) {
            // reset
            Log.d(TAG, "Reset results list view");
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = resultsInitialHeight;
            params.width = resultsInitialWidth;
            listView.setLayoutParams(params);
            ratioSet = false;

        } else {
            // set initial
            Log.d(TAG, "Initialise results list view");
            resultsInitialHeight = listView.getHeight();
            resultsInitialWidth = listView.getWidth();
            initialised = true;
        }

        ratioApplied = false;

        Log.d(TAG, "resultsInitialHeight: " + resultsInitialHeight);
        Log.d(TAG, "resultsInitialWidth: " + resultsInitialWidth);
    }

    /*
    public void testing() {

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        listView.setLayoutParams(applyRatioToLayout(params, 100, 100, 1));
    }
    */

    /**
     *
     */
    public void updateResultsListLayout() {

        if(initialised && ratioSet && !ratioApplied) {

            if(listView == null) {
                listView = (ListView) getActivity().findViewById(R.id.listView);
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            listView.setLayoutParams(applyRatioToLayout(params, resultsInitialHeight, resultsInitialWidth, ratio));

            ratioApplied = true;
        }
    }

    /**
     *
     * @param params
     * @param height
     * @param width
     * @param ratio
     * @return
     */
    private ViewGroup.LayoutParams applyRatioToLayout(ViewGroup.LayoutParams params, int height, int width, float ratio) {

        if (height > width) {
            // reduce height
            height = Math.round(height * ratio);
        } else if (height == 0) { // TODO fix size
            height = Math.round(width * ratio);
        } else {
            // reduce width
            width = Math.round(width * ratio);
        }

        params.height = height;
        params.width = width;

        Log.d(TAG, "params.height: " + params.height);
        Log.d(TAG, "params.width: " + params.width);

        return params;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
        ratioSet = true;

        Log.d(TAG, "ratio: " + ratio);
    }

    // TODO BIBLECAST#6 - move to fragment
}
