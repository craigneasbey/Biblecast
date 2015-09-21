package au.id.neasbey.biblecast.API.BiblesOrg;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIResponse;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.BiblecastException;
import au.id.neasbey.biblecast.model.BibleVersion;
import au.id.neasbey.biblecast.R;
import au.id.neasbey.biblecast.util.SequenceNumber;
import au.id.neasbey.biblecast.util.UIUtils;

/**
 * Created by craigneasbey on 30/06/15.
 *
 * Bibles.org Bible API response parser utility
 */
public class BibleAPIResponseParserBiblesOrg extends BibleAPIResponseParser {

    private static final String responseKey = "response";
    private static final String searchKey = "search";
    private static final String resultKey = "result";
    private static final String typeKey = "type";
    private static final String passagesKey = "passages";
    private static final String versesKey = "verses";
    private static final String referenceKey = "reference";
    private static final String textKey = "text";
    private static final String versionsKey = "versions";
    private static final String idKey = "id";
    private static final String nameKey = "name";
    private static final String abbreviationKey = "abbreviation";
    private static final String booksKey = "books";

    /**
     * Checks the response code from the Bibles.org Bible API server
     *
     * @throws BiblecastException
     */
    @Override
    public void parseResponseStatus(int responseCode, String responseMessage) throws BiblecastException {
        if (responseCode == BibleAPIResponse.RESPONSE_CODE_UNAUTHORIZED) {
            throw new BiblecastException(UIUtils.getContext().getString(R.string.api_bible_org_incorrect));
        }

        if (responseCode != BibleAPIResponse.RESPONSE_CODE_OK) {
            throw new BiblecastException(responseCode + " - " + responseMessage);
        }
    }

    /**
     * Parse the response string
     *
     * @param responseString Response string
     * @return Result list after parsing the response string
     * @throws BiblecastException
     */
    @Override
    public List<Spanned> parseResponseDataToSpannedList(String responseString) throws BiblecastException {
        return parseJSONToList(responseString);
    }

    @Override
    public List<BibleVersion> parseResponseDataToVersionList(String responseString) throws BiblecastException {
        return parseJSONToVersionList(responseString);
    }

    @Override
    public List<String> parseResponseDataToStringList(String responseString) throws BiblecastException {
        return parseJSONToStringList(responseString);
    }

    /**
     * Parse the JSON response from bibles.org
     *
     * @param jsonString JSON response in format: http://bibles.org/pages/api/documentation/search
     * @return List of HTML elements
     * @throws Exception Allows JSON parse exceptions to be translated for the app
     */
    public List<Spanned> parseJSONToList(String jsonString) throws BiblecastException {

        SequenceNumber sq = new SequenceNumber();

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
                                results = parsePassages(resultList, resultValues.optJSONArray(passagesKey), sq);
                                break;
                            case versesKey:
                                // Get verses values
                                results = parseVerses(resultList, resultValues.optJSONArray(versesKey), sq);
                                break;
                            default:
                                results = parseVerses(resultList, resultValues.optJSONArray(versesKey), sq);
                        }
                    }
                }
            }

            if (!results) {
                throw new BiblecastException(UIUtils.getContext().getString(R.string.api_no_results));
            }
        } catch (JSONException e) {

            throw new BiblecastException(e.getMessage());
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
    private boolean parsePassages(List<Spanned> resultList, JSONArray passagesValues, SequenceNumber sq) throws JSONException {
        boolean results = false;

        if (passagesValues != null) {
            int passagesLength = passagesValues.length();

            for (int i = 0; i < passagesLength; i++) {

                JSONObject passageValues = passagesValues.optJSONObject(i);

                if (passageValues != null) {
                    String passageText = passageValues.getString(textKey);

                    if (passageText != null) {
                        results = true;

                        // Add new results to the list,
                        for(String paragraph : passageText.split("\\n")) {
                            //resultList.add(Html.fromHtml(HttpUtils.addAnchors(paragraph, sq)));
                            resultList.add(Html.fromHtml(paragraph));
                        }
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
    private boolean parseVerses(List<Spanned> resultList, JSONArray versesValues, SequenceNumber sq) throws JSONException {
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
                        //resultList.add(Html.fromHtml(HttpUtils.addAnchors(referenceText + " " + verseText, sq)));
                        resultList.add(Html.fromHtml(referenceText + " " + verseText));

                        results = true;
                    }
                }
            }
        }

        return results;
    }

    private List<BibleVersion> parseJSONToVersionList(String jsonString) throws BiblecastException {

        List<BibleVersion> resultList = new LinkedList<>();

        try {
            boolean results = false;

            // Creates a new JSONObject with name/value mappings from the JSON string
            JSONObject jsonValues = new JSONObject(jsonString);

            // Get response values
            JSONObject responseValues = jsonValues.optJSONObject(responseKey);

            // Get versions values
            if (responseValues != null) {
                results = parseVersions(resultList, responseValues.optJSONArray(versionsKey));
            }

            if (!results) {
                throw new BiblecastException(UIUtils.getContext().getString(R.string.api_no_results));
            }
        } catch (JSONException e) {

            throw new BiblecastException(e.getMessage());
        }

        return resultList;
    }

    private boolean parseVersions(List<BibleVersion> resultList, JSONArray versionsValues) throws JSONException {
        boolean results = false;

        if (versionsValues != null) {
            int versionsLength = versionsValues.length();

            for (int i = 0; i < versionsLength; i++) {

                JSONObject versionValues = versionsValues.optJSONObject(i);

                if (versionValues != null) {
                    String idText = versionValues.getString(idKey);
                    String nameText = versionValues.getString(nameKey);
                    String abbreviation = versionValues.getString(abbreviationKey);

                    if (idText != null && nameText != null && abbreviation != null) {
                        // Add new results to the list
                        BibleVersion bibleVersion = new BibleVersion();
                        bibleVersion.setId(idText);
                        bibleVersion.setName(nameText);
                        bibleVersion.setAbbreviation(abbreviation);
                        resultList.add(bibleVersion);

                        results = true;
                    }
                }
            }
        }

        return results;
    }

    private List<String> parseJSONToStringList(String jsonString) throws BiblecastException {
        List<String> resultList = new LinkedList<>();

        try {
            boolean results = false;

            // Creates a new JSONObject with name/value mappings from the JSON string
            JSONObject jsonValues = new JSONObject(jsonString);

            // Get response values
            JSONObject responseValues = jsonValues.optJSONObject(responseKey);

            // Get versions values
            if (responseValues != null) {
                results = parseBooks(resultList, responseValues.optJSONArray(booksKey));
            }

            if (!results) {
                throw new BiblecastException(UIUtils.getContext().getString(R.string.api_no_results));
            }
        } catch (JSONException e) {

            throw new BiblecastException(e.getMessage());
        }

        return resultList;
    }

    private boolean parseBooks(List<String> resultList, JSONArray booksValues) throws JSONException {
        boolean results = false;

        if (booksValues != null) {
            int booksLength = booksValues.length();

            for (int i = 0; i < booksLength; i++) {

                JSONObject bookValues = booksValues.optJSONObject(i);

                if (bookValues != null) {
                    String nameText = bookValues.getString(nameKey);

                    if (nameText != null) {
                        // Add new results to the list
                        resultList.add(nameText);

                        results = true;
                    }
                }
            }
        }

        return results;
    }
}
