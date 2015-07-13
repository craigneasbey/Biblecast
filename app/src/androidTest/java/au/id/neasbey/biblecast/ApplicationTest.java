package au.id.neasbey.biblecast;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.util.BibleAPIResponseParser;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testBase64() {
        String apiToken = "989879879jhljhlsjdfalsjbfebb2l3ib2lj3:";

        String basicAuth = "Basic " + new String(android.util.Base64.encode(apiToken.getBytes(), Base64.NO_WRAP));

        assertEquals("Basic OTg5ODc5ODc5amhsamhsc2pkZmFsc2piZmViYjJsM2liMmxqMzo=", basicAuth);
    }

    @Test
    public void testParseJSONToListSuccessful() {
        String jsonText;
        List<Spanned> expectedList = null;
        List<Spanned> actualList = new LinkedList<>();

        jsonText = createJSON(expectedList);

        BibleAPIResponseParser objectBeingTest = new BibleAPIResponseParser();

        try {
            objectBeingTest.parseJSONToList(jsonText, actualList);
        } catch(Exception e) {
            e.printStackTrace();
        }

        assertEquals(expectedList, actualList);
    }

    private String createJSON(List<Spanned> expectedList) {

        // https://bibles.org/v2/search.js?query=John+3%3A16&version=eng-KJV
        String jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"passages\",\"passages\":[{\"display\":\"John 3:16\",\"version\":\"eng-KJV\",\"version_abbreviation\":\"KJV\",\"path\":\"\\/chapters\\/eng-KJV:John.3\\/verses.js?start=16&end=16\",\"start_verse_id\":\"eng-KJV:John.3.16\",\"end_verse_id\":\"eng-KJV:John.3.16\",\"text\":\"<p class=\\\"p\\\"><sup id=\\\"John.3.16\\\" class=\\\"v\\\">16<\\/sup>For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.<\\/p>\",\"copyright\":\"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions \\u00a9 2011 British and Foreign Bible Society; Crown Copyright in UK<\\/p>\"}]}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('55933eca5d4957.03024947');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"55933eca5d4957.03024947\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55933eca5d4957.03024947'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";

        expectedList = new LinkedList<>();
        expectedList.add(Html.fromHtml("<p class=\\\"p\\\"><sup id=\\\"John.3.16\\\" class=\\\"v\\\">16<\\/sup>For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.<\\/p>"));

        return jsonResponse;
    }
}