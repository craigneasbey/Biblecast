package au.id.neasbey.biblecast.util;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by craigneasbey on 30/06/15.
 */
public class BibleAPIResponseParser {

    private final String responseKey = "response";
    private final String searchKey = "search";
    private final String resultKey = "result";
    private final String passagesKey = "passages";
    private final String textKey = "text";

    /**
     * Parse the JSON response from bibles.org
     * @param jsonString JSON response in format: http://bibles.org/pages/api/documentation/search
     * @param spannedList List of HTML elements
     * @throws Exception Allows JSON parse exceptions to be translated for the app
     */
    public void parseJSONToList(String jsonString, List<Spanned> spannedList) throws Exception {

        try {

            boolean results = false;

            // Creates a new JSONObject with name/value mappings from the JSON string
            JSONObject jsonValues = new JSONObject(jsonString);

            // Get response values
            JSONObject responseValues = jsonValues.optJSONObject(responseKey);

            // Get search values
            if(responseValues != null) {
                JSONObject searchValues = responseValues.optJSONObject(searchKey);

                // Get result values
                if(searchValues != null) {
                    JSONObject resultValues = searchValues.optJSONObject(resultKey);

                    // Get passages values
                    if(resultValues != null) {
                        JSONArray passagesValues = resultValues.optJSONArray(passagesKey);

                        if(passagesValues != null) {
                            int passagesLength = passagesValues.length();
                            boolean first = true;

                            for (int i = 0; i < passagesLength; i++) {
                                if (first) {
                                    // Remove all entries from the list
                                    spannedList.clear();

                                    first = false;
                                }

                                JSONObject passageValues = passagesValues.optJSONObject(i);

                                if (passageValues != null) {
                                    String passageText = passageValues.getString(textKey);

                                    if (passageText != null) {
                                        // Add new results to the list
                                        spannedList.add(Html.fromHtml(passageText));

                                        results = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(!results) {
                throw new Exception("No results found");
            }
        } catch (JSONException e) {

            throw new Exception(e.getMessage());
        }
    }
}
