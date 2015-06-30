package au.id.neasbey.biblecast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class BibleSearch extends AppCompatActivity {

    private static final String TAG = "BibleSearch";
    private final String apiURL = "https://bibles.org/v2/search.js";
    private final String apiToken = "0W0u6rIgW1D8P8qyevum7kmwspcVdLRYWBzqcpcs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_search);

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BibleAPI().execute(apiURL, apiToken);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bible_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Activity getActivity() {
        return this;
    }

    private class BibleAPI extends AsyncTask<String, String, String> {

        private SearchView searchView = (SearchView) findViewById(R.id.searchView);
        private ListView listView = (ListView) findViewById(R.id.listView);
        private ProgressDialog progressDialog = new ProgressDialog(BibleSearch.this);

        private String parameters = "";
        private String bibleVersions = "eng-KJV";
        private String content = "";

        protected void onPreExecute() {
            //Start Progress Dialog (Message)
            progressDialog.setMessage("Please wait..");
            progressDialog.show();

            try {
                // Set Request parameter
                parameters += "?query=" + URLEncoder.encode(searchView.getQuery().toString(), "UTF-8");
                parameters += "&version=" + URLEncoder.encode(bibleVersions, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0] + parameters; // API URL and data
            String auth = params[1]; // API token
            String resultToDisplay = "";

            BufferedReader reader = null;

            Log.i(TAG, "Request: " + urlString);

            try {

                // Send HTTP Get request
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String basicAuth = "Basic " + new String(android.util.Base64.encode(auth.getBytes(), android.util.Base64.NO_WRAP));
                urlConnection.addRequestProperty("Authorization", basicAuth);

                // Get server response
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line);
                    sb.append("\n");
                }

                // Append Server Response To Content String
                content = sb.toString();

                Log.i(TAG, "Response: " + content);

            } catch (Exception e) {
                resultToDisplay = e.getMessage();
            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to close input reader: " + ex.getMessage());
                }
            }

            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            // Close progress dialog
            progressDialog.dismiss();

            // Handle error
            if(result != null && !result.isEmpty()) {
                Log.e(TAG, "Request Failed: " + result);

                displayError(getActivity(), R.string.api_failed, result);
            } else if(result != null && !content.isEmpty()) {
                Log.i(TAG, "Request Successful");

                // Remove existing results
                listView.removeAllViews();

                // Add new results to the list
                for (String line : content.split("\n")) {
                    TextView textItem = new TextView(BibleSearch.this);
                    textItem.setText(line);
                    listView.addView(textItem);
                }
            } else {
                Log.e(TAG, "No result returned");

                displayError(getActivity(), R.string.api_no_results, "No result returned");
            }
        }

        private void displayError(Context context, int title, String message) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // Add the OK button
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });

            builder.setTitle(title).setMessage(message);
            builder.create().show();
        }
    }
}
