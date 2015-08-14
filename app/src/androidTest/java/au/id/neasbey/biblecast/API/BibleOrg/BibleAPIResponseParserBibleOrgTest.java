package au.id.neasbey.biblecast.API.BibleOrg;

import android.text.Html;
import android.text.Spanned;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIResponseParser;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Test the Bible,Org Bible API response parser from JSON to list
 */
public class BibleAPIResponseParserBibleOrgTest extends TestCase {

    public static final String passageType = "passage";

    public static final String verseType = "verse";

    private BibleAPIResponseParser objectUnderTest;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        objectUnderTest = new BibleAPIResponseParserBibleOrg();
    }

    /**
     * Create test JSON data and expected spanned list
     *
     * @param expectedList Excepted list of spanned HTML
     * @param type Result type, passage or verse
     * @param successful Is success or failure expected?
     * @return JSON response text
     */
    public static String createJSONAndListHTML(List<Spanned> expectedList, String type, boolean successful) {
        String jsonResponse = "";

        // Create test JSON data of type, complete if successful, otherwise with missing elements
        switch(type) {
            case passageType:
                // https://bibles.org/v2/search.js?query=John+3%3A16&version=eng-KJV
                if (successful) {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"passages\",\"passages\":[{\"display\":\"John 3:16\",\"version\":\"eng-KJV\",\"version_abbreviation\":\"KJV\",\"path\":\"\\/chapters\\/eng-KJV:John.3\\/verses.js?start=16&end=16\",\"start_verse_id\":\"eng-KJV:John.3.16\",\"end_verse_id\":\"eng-KJV:John.3.16\",\"text\":\"<p class=\\\"p\\\"><sup id=\\\"John.3.16\\\" class=\\\"v\\\">16<\\/sup>For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.<\\/p>\",\"copyright\":\"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions \\u00a9 2011 British and Foreign Bible Society; Crown Copyright in UK<\\/p>\"}]}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('55933eca5d4957.03024947');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"55933eca5d4957.03024947\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55933eca5d4957.03024947'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
                } else {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"passages\"}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('55933eca5d4957.03024947');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"55933eca5d4957.03024947\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55933eca5d4957.03024947'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
                }
                break;
            case verseType:
                // https://bibles.org/v2/search.js?query=fear&version=eng-KJV
                if(successful) {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"verses\",\"summary\":{\"query\":\"fear\",\"start\":1,\"total\":480,\"rpp\":\"15\",\"sort\":\"relevance\",\"versions\":[\"eng-KJV\"],\"testaments\":[\"OT\",\"NT\"]},\"spelling\":[],\"verses\":[{\"auditid\":\"0\",\"verse\":\"18\",\"lastverse\":\"18\",\"id\":\"eng-KJV:1John.4.18\",\"osis_end\":\"eng-KJV:1John.4.18\",\"label\":\"1John.004.018,eng-KJV\",\"reference\":\"1 John 4:18\",\"prev_osis_id\":\"1John.004.017,eng-KJV\",\"next_osis_id\":\"1John.004.019,eng-KJV\",\"text\":\"<sup>18<\\/sup>There is no <em>fear<\\/em> in love; but perfect love casteth out <em>fear<\\/em>: because <em>fear<\\/em> hath torment. He that feareth is not made perfect in love.\",\"parent\":{\"chapter\":{\"path\":\"\\/chapters\\/eng-KJV:1John.4\",\"name\":\"1 John 4\",\"id\":\"eng-KJV:1John.4\"}},\"next\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:1John.4.19\",\"name\":\"1 John 4:19\",\"id\":\"eng-KJV:1John.4.19\"}},\"previous\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:1John.4.17\",\"name\":\"1 John 4:17\",\"id\":\"eng-KJV:1John.4.17\"}},\"copyright\":\"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions \\u00a9 2011 British and Foreign Bible Society; Crown Copyright in UK<\\/p>\"},{\"auditid\":\"0\",\"verse\":\"43\",\"lastverse\":\"43\",\"id\":\"eng-KJV:Acts.2.43\",\"osis_end\":\"eng-KJV:Acts.2.43\",\"label\":\"Acts.002.043,eng-KJV\",\"reference\":\"Acts 2:43\",\"prev_osis_id\":\"Acts.002.042,eng-KJV\",\"next_osis_id\":\"Acts.002.044,eng-KJV\",\"text\":\"<sup>43<\\/sup>And <em>fear<\\/em> came upon every soul: and many wonders and signs were done by the apostles.\",\"parent\":{\"chapter\":{\"path\":\"\\/chapters\\/eng-KJV:Acts.2\",\"name\":\"Acts 2\",\"id\":\"eng-KJV:Acts.2\"}},\"next\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:Acts.2.44\",\"name\":\"Acts 2:44\",\"id\":\"eng-KJV:Acts.2.44\"}},\"previous\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:Acts.2.42\",\"name\":\"Acts 2:42\",\"id\":\"eng-KJV:Acts.2.42\"}},\"copyright\":\"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions \\u00a9 2011 British and Foreign Bible Society; Crown Copyright in UK<\\/p>\"}]}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('559b3a734ebe94.13992555');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"559b3a734ebe94.13992555\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('559b3a734ebe94.13992555'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
                } else {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"verses\",\"summary\":{\"query\":\"fear\",\"start\":1,\"total\":480,\"rpp\":\"15\",\"sort\":\"relevance\",\"versions\":[\"eng-KJV\"],\"testaments\":[\"OT\",\"NT\"]},\"spelling\":[],\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('559b3a734ebe94.13992555');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"559b3a734ebe94.13992555\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('559b3a734ebe94.13992555'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
                }
                break;
        }

        if(expectedList != null) {
            switch (type) {
                case passageType:
                    // Add expected spanned list
                    expectedList.add(Html.fromHtml("<p class=\\\"p\\\"><sup id=\\\"John.3.16\\\" class=\\\"v\\\">16<\\/sup>For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.<\\/p>"));
                    break;
                case verseType:
                    // Add expected spanned list
                    expectedList.add(Html.fromHtml("1 John 4:18 <sup>18</sup>There is no <em>fear</em> in love; but perfect love casteth out <em>fear</em>: because <em>fear</em> hath torment. He that feareth is not made perfect in love."));
                    expectedList.add(Html.fromHtml("Acts 2:43 <sup>43</sup>And <em>fear</em> came upon every soul: and many wonders and signs were done by the apostles."));
                    break;
            }
        }

        return jsonResponse;
    }

    public void testParsePassagesJSONResultToListSuccessful() {
        List<Spanned> expectedList = new LinkedList<>();
        List<Spanned> actualList = new LinkedList<>();
        String jsonTextToParse;

        jsonTextToParse = createJSONAndListHTML(expectedList, passageType, true);

        try {
            objectUnderTest.parseResponseToList(jsonTextToParse, actualList);
        } catch(Exception e) {
            e.printStackTrace();
        }

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    public void testParsePassagesJSONResultToListFailed() {
        List<Spanned> expectedList = new LinkedList<>();
        List<Spanned> actualList = new LinkedList<>();
        String jsonTextToParse;

        jsonTextToParse = createJSONAndListHTML(expectedList, passageType, false);

        try {
            objectUnderTest.parseResponseToList(jsonTextToParse, actualList);
        } catch(Exception e) {
            e.printStackTrace();
        }

        assertFalse(expectedList.size() == actualList.size());
        assertFalse(expectedList.toString().compareTo(actualList.toString()) == 0);
    }

    public void testParseVersesJSONResultToListSuccessful() {
        List<Spanned> expectedList = new LinkedList<>();
        List<Spanned> actualList = new LinkedList<>();
        String jsonTextToParse;

        jsonTextToParse = createJSONAndListHTML(expectedList, verseType, true);

        try {
            objectUnderTest.parseResponseToList(jsonTextToParse, actualList);
        } catch(Exception e) {
            e.printStackTrace();
        }

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    public void testParseVersesJSONResultToListFailed() {
        List<Spanned> expectedList = new LinkedList<>();
        List<Spanned> actualList = new LinkedList<>();
        String jsonTextToParse;

        jsonTextToParse = createJSONAndListHTML(expectedList, verseType, false);

        try {
            objectUnderTest.parseResponseToList(jsonTextToParse, actualList);
        } catch(Exception e) {
            e.printStackTrace();
        }

        assertFalse(expectedList.size() == actualList.size());
        assertFalse(expectedList.toString().compareTo(actualList.toString()) == 0);
    }
}
