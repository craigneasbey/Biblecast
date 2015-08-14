package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.API.BibleSearchAPIException;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 30/06/15.
 *
 * Bible.org Bible API response parser utility
 */
public class BibleAPIResponseParserBibleOrg extends BibleAPIResponseParser {

    private static final String responseKey = "response";
    private static final String searchKey = "search";
    private static final String resultKey = "result";
    private static final String typeKey = "type";
    private static final String passagesKey = "passages";
    private static final String versesKey = "verses";
    private static final String referenceKey = "reference";
    private static final String textKey = "text";

    /**
     * Parse the response string
     *
     * @param responseString Response string
     * @param resultList Result list after parsing the response string
     * @throws BibleSearchAPIException
     */
    @Override
    public void parseResponseToList(String responseString, List<Spanned> resultList) throws BibleSearchAPIException {
        parseJSONToList(responseString, resultList);
    }

    /**
     * Parse the JSON response from bibles.org
     *
     * @param jsonString  JSON response in format: http://bibles.org/pages/api/documentation/search
     * @param spannedList List of HTML elements
     * @throws Exception Allows JSON parse exceptions to be translated for the app
     */
    public void parseJSONToList(String jsonString, List<Spanned> spannedList) throws BibleSearchAPIException {

        try {
            boolean results = false;

            // Creates a new JSONObject with name/value mappings from the JSON string
            JSONObject jsonValues = new JSONObject(jsonString);

            // Get response values
            JSONObject responseValues = jsonValues.optJSONObject(responseKey);

            // Get search values
            if (responseValues != null) {
                JSONObject searchValues = responseValues.optJSONObject(searchKey);

                // Get result values
                if (searchValues != null) {
                    JSONObject resultValues = searchValues.optJSONObject(resultKey);

                    // Get type value
                    if (resultValues != null) {
                        String typeValue = resultValues.optString(typeKey);

                        // check type
                        switch (typeValue) {
                            case passagesKey:
                                // Get passages values
                                results = parsePassages(spannedList, resultValues.optJSONArray(passagesKey));
                                break;
                            case versesKey:
                                // Get verses values
                                results = parseVerses(spannedList, resultValues.optJSONArray(versesKey));
                                break;
                            default:
                                results = parseVerses(spannedList, resultValues.optJSONArray(versesKey));
                        }
                    }
                }
            }

            if (!results) {
                throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_no_results));
            }
        } catch (JSONException e) {

            throw new BibleSearchAPIException(e.getMessage());
        }
    }

    /**
     * Parses bible results with passages.  Adds each passage to the display list.
     *
     * @param spannedList    Display list
     * @param passagesValues JSONArray of passages
     * @return {@code Boolean.TRUE} if results exist, otherwise {@code Boolean.FALSE}
     * @throws JSONException
     */
    private boolean parsePassages(List<Spanned> spannedList, JSONArray passagesValues) throws JSONException {
        boolean results = false;

        if (passagesValues != null) {
            int passagesLength = passagesValues.length();
            boolean first = true;

            for (int i = 0; i < passagesLength; i++) {
                if (first) {
                    // Remove all entries from the list.  This will clear the results on the screen
                    spannedList.clear();

                    first = false;
                }

                JSONObject passageValues = passagesValues.optJSONObject(i);

                if (passageValues != null) {
                    String passageText = passageValues.getString(textKey);

                    if (passageText != null) {
                        // Add new results to the list,
                        spannedList.add(Html.fromHtml(passageText));

                        results = true;
                    }
                }
            }
        }

        return results;
    }

    /**
     * Parses bible results with verses.  Adds each verse to the display list.
     *
     * @param spannedList  Display list
     * @param versesValues JSONArray of verses
     * @return {@code Boolean.TRUE} if results exist, otherwise {@code Boolean.FALSE}
     * @throws JSONException
     */
    private boolean parseVerses(List<Spanned> spannedList, JSONArray versesValues) throws JSONException {
        boolean results = false;

        if (versesValues != null) {
            int versesLength = versesValues.length();
            boolean first = true;

            for (int i = 0; i < versesLength; i++) {
                if (first) {
                    // Remove all entries from the list, This will clear the results on the screen
                    spannedList.clear();

                    first = false;
                }

                JSONObject verseValues = versesValues.optJSONObject(i);

                if (verseValues != null) {
                    String referenceText = verseValues.getString(referenceKey);
                    String verseText = verseValues.getString(textKey);

                    if (referenceText != null && verseText != null) {
                        // Add new results to the list
                        spannedList.add(Html.fromHtml(referenceText + " " + verseText));

                        results = true;
                    }
                }
            }
        }

        return results;
    }
}
