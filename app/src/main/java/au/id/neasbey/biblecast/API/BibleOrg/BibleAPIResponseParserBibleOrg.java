package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIResponse;
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
     * Checks the response code from the Bible.org Bible API server
     *
     * @throws BibleSearchAPIException
     */
    @Override
    public void parseResponseStatus(int responseCode, String responseMessage) throws BibleSearchAPIException {
        if (responseCode == BibleAPIResponse.responseCodeUnauthorized) {
            throw new BibleSearchAPIException(UIUtils.getContext().getString(R.string.api_bible_org_incorrect));
        }

        if (responseCode != BibleAPIResponse.responseCodeOk) {
            throw new BibleSearchAPIException(responseCode + " - " + responseMessage);
        }
    }

    /**
     * Parse the response string
     *
     * @param responseString Response string
     * @return Result list after parsing the response string
     * @throws BibleSearchAPIException
     */
    @Override
    public List<Spanned> parseResponseDataToList(String responseString) throws BibleSearchAPIException {
        return parseJSONToList(responseString);
    }

    /**
     * Parse the JSON response from bibles.org
     *
     * @param jsonString JSON response in format: http://bibles.org/pages/api/documentation/search
     * @return List of HTML elements
     * @throws Exception Allows JSON parse exceptions to be translated for the app
     */
    public List<Spanned> parseJSONToList(String jsonString) throws BibleSearchAPIException {

        List<Spanned> resultList = new LinkedList<>();

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
                                results = parsePassages(resultList, resultValues.optJSONArray(passagesKey));
                                break;
                            case versesKey:
                                // Get verses values
                                results = parseVerses(resultList, resultValues.optJSONArray(versesKey));
                                break;
                            default:
                                results = parseVerses(resultList, resultValues.optJSONArray(versesKey));
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

        return resultList;
    }

    /**
     * Parses bible results with passages.  Adds each passage to the results list.
     *
     * @param resultList Results list
     * @param passagesValues JSONArray of passages
     * @return {@code Boolean.TRUE} if results exist, otherwise {@code Boolean.FALSE}
     * @throws JSONException
     */
    private boolean parsePassages(List<Spanned> resultList, JSONArray passagesValues) throws JSONException {
        boolean results = false;

        if (passagesValues != null) {
            int passagesLength = passagesValues.length();

            for (int i = 0; i < passagesLength; i++) {

                JSONObject passageValues = passagesValues.optJSONObject(i);

                if (passageValues != null) {
                    String passageText = passageValues.getString(textKey);

                    if (passageText != null) {
                        // Add new results to the list,
                        resultList.add(Html.fromHtml(passageText));

                        results = true;
                    }
                }
            }
        }

        return results;
    }

    /**
     * Parses bible results with verses.  Adds each verse to the results list.
     *
     * @param resultList Results list
     * @param versesValues JSONArray of verses
     * @return {@code Boolean.TRUE} if results exist, otherwise {@code Boolean.FALSE}
     * @throws JSONException
     */
    private boolean parseVerses(List<Spanned> resultList, JSONArray versesValues) throws JSONException {
        boolean results = false;

        if (versesValues != null) {
            int versesLength = versesValues.length();

            for (int i = 0; i < versesLength; i++) {

                JSONObject verseValues = versesValues.optJSONObject(i);

                if (verseValues != null) {
                    String referenceText = verseValues.getString(referenceKey);
                    String verseText = verseValues.getString(textKey);

                    if (referenceText != null && verseText != null) {
                        // Add new results to the list
                        resultList.add(Html.fromHtml(referenceText + " " + verseText));

                        results = true;
                    }
                }
            }
        }

        return results;
    }
}
