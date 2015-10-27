package au.id.neasbey.biblecast.API.BiblesOrg;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.LinkedList;
import java.util.List;

import au.id.neasbey.biblecast.API.BibleAPIResponse;
import au.id.neasbey.biblecast.API.BibleAPIResponseParser;
import au.id.neasbey.biblecast.BuildConfig;
import au.id.neasbey.biblecast.exception.BiblecastException;
import au.id.neasbey.biblecast.model.BibleVersion;
import au.id.neasbey.biblecast.util.UIUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Test the Bible,Org Bible API response parser from JSON to list
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class BibleAPIResponseParserBiblesOrgTest {

    public static final String passageType = "passage";

    public static final String verseType = "verse";

    private BibleAPIResponseParser objectUnderTest;

    @BeforeClass
    public static void setupRobolectric() {
        ShadowLog.stream = System.out;
    }

    @Before
    public void setUp() throws Exception {
        objectUnderTest = new BibleAPIResponseParserBiblesOrg();
    }

    /**
     * Create test JSON data
     *
     * @param keyType Result key type, passage or verse
     * @param successful Is success or failure expected?
     * @return JSON response text
     */
    public static String createSearchJSON(String keyType, boolean successful) {
        String jsonResponse = "";

        // Create test JSON data of type, complete if successful, otherwise with missing elements
        switch (keyType) {
            case passageType:
                // https://bibles.org/v2/search.js?query=john 3:1-8&version=eng-KJV
                if (successful) {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"passages\",\"passages\":[{\"display\":\"John 3:1-8\",\"version\":\"eng-KJV\",\"version_abbreviation\":\"KJV\",\"path\":\"\\/chapters\\/eng-KJV:John.3\\/verses.js?start=1&end=8\",\"start_verse_id\":\"eng-KJV:John.3.1\",\"end_verse_id\":\"eng-KJV:John.3.8\",\"text\":\"<h4 class=\\\"s2\\\">Nicodemus<\\/h4>\\n<p class=\\\"p\\\"><sup id=\\\"John.3.1\\\" class=\\\"v\\\">1<\\/sup>There was a man of the Pharisees, named Nicodemus, a ruler of the Jews:<sup id=\\\"John.3.2\\\" class=\\\"v\\\">2<\\/sup>the same came to Jesus by night, and said unto him, Rabbi, we know that thou art a teacher come from God: for no man can do these miracles that thou doest, except God be with him.<sup id=\\\"John.3.3\\\" class=\\\"v\\\">3<\\/sup>Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.<\\/p>\\n<p class=\\\"p\\\"><sup id=\\\"John.3.4\\\" class=\\\"v\\\">4<\\/sup>Nicodemus saith unto him, How can a man be born when he is old? can he enter the second time into his mother's womb, and be born?<sup id=\\\"John.3.5\\\" class=\\\"v\\\">5<\\/sup>Jesus answered, Verily, verily, I say unto thee, Except a man be born of water and <span class=\\\"add\\\">of<\\/span> the Spirit, he cannot enter into the kingdom of God.<sup id=\\\"John.3.6\\\" class=\\\"v\\\">6<\\/sup>That which is born of the flesh is flesh; and that which is born of the Spirit is spirit.<sup id=\\\"John.3.7\\\" class=\\\"v\\\">7<\\/sup>Marvel not that I said unto thee, Ye must be born again.<sup id=\\\"John.3.8\\\" class=\\\"v\\\">8<\\/sup>The wind bloweth where it listeth, and thou hearest the sound thereof, but canst not tell whence it cometh, and whither it goeth: so is every one that is born of the Spirit.<\\/p>\",\"copyright\":\"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions \\u00a9 2011 British and Foreign Bible Society; Crown Copyright in UK<\\/p>\"}]}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('55e7c8089bab20.07903825');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55e7c8089bab20.07903825\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"55e7c8089bab20.07903825\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55e7c8089bab20.07903825'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55e7c8089bab20.07903825\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
                } else {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"passages\"}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('55933eca5d4957.03024947');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"55933eca5d4957.03024947\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55933eca5d4957.03024947'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55933eca5d4957.03024947\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
                }
                break;
            case verseType:
                // https://bibles.org/v2/search.js?query=fear&version=eng-KJV
                if (successful) {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"verses\",\"summary\":{\"query\":\"fear\",\"start\":1,\"total\":480,\"rpp\":\"15\",\"sort\":\"relevance\",\"versions\":[\"eng-KJV\"],\"testaments\":[\"OT\",\"NT\"]},\"spelling\":[],\"verses\":[{\"auditid\":\"0\",\"verse\":\"18\",\"lastverse\":\"18\",\"id\":\"eng-KJV:1John.4.18\",\"osis_end\":\"eng-KJV:1John.4.18\",\"label\":\"1John.004.018,eng-KJV\",\"reference\":\"1 John 4:18\",\"prev_osis_id\":\"1John.004.017,eng-KJV\",\"next_osis_id\":\"1John.004.019,eng-KJV\",\"text\":\"<sup>18<\\/sup>There is no <em>fear<\\/em> in love; but perfect love casteth out <em>fear<\\/em>: because <em>fear<\\/em> hath torment. He that feareth is not made perfect in love.\",\"parent\":{\"chapter\":{\"path\":\"\\/chapters\\/eng-KJV:1John.4\",\"name\":\"1 John 4\",\"id\":\"eng-KJV:1John.4\"}},\"next\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:1John.4.19\",\"name\":\"1 John 4:19\",\"id\":\"eng-KJV:1John.4.19\"}},\"previous\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:1John.4.17\",\"name\":\"1 John 4:17\",\"id\":\"eng-KJV:1John.4.17\"}},\"copyright\":\"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions \\u00a9 2011 British and Foreign Bible Society; Crown Copyright in UK<\\/p>\"},{\"auditid\":\"0\",\"verse\":\"43\",\"lastverse\":\"43\",\"id\":\"eng-KJV:Acts.2.43\",\"osis_end\":\"eng-KJV:Acts.2.43\",\"label\":\"Acts.002.043,eng-KJV\",\"reference\":\"Acts 2:43\",\"prev_osis_id\":\"Acts.002.042,eng-KJV\",\"next_osis_id\":\"Acts.002.044,eng-KJV\",\"text\":\"<sup>43<\\/sup>And <em>fear<\\/em> came upon every soul: and many wonders and signs were done by the apostles.\",\"parent\":{\"chapter\":{\"path\":\"\\/chapters\\/eng-KJV:Acts.2\",\"name\":\"Acts 2\",\"id\":\"eng-KJV:Acts.2\"}},\"next\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:Acts.2.44\",\"name\":\"Acts 2:44\",\"id\":\"eng-KJV:Acts.2.44\"}},\"previous\":{\"verse\":{\"path\":\"\\/verses\\/eng-KJV:Acts.2.42\",\"name\":\"Acts 2:42\",\"id\":\"eng-KJV:Acts.2.42\"}},\"copyright\":\"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions \\u00a9 2011 British and Foreign Bible Society; Crown Copyright in UK<\\/p>\"}]}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('559b3a734ebe94.13992555');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"559b3a734ebe94.13992555\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('559b3a734ebe94.13992555'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
                } else {
                    jsonResponse = "{\"response\":{\"search\":{\"result\":{\"type\":\"verses\",\"summary\":{\"query\":\"fear\",\"start\":1,\"total\":480,\"rpp\":\"15\",\"sort\":\"relevance\",\"versions\":[\"eng-KJV\"],\"testaments\":[\"OT\",\"NT\"]},\"spelling\":[],\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('559b3a734ebe94.13992555');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"559b3a734ebe94.13992555\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('559b3a734ebe94.13992555'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=559b3a734ebe94.13992555\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}}}";
                }
                break;
        }

        return jsonResponse;
    }

    /**
     * Create test search list data
     *
     * @param keyType Result key type, passage or verse
     * @return A list of spanned test HTML
     */
    public static List<Spanned> createSearchList(String keyType) {
        List<Spanned> spannedList = new LinkedList<>();

        switch (keyType) {
            case passageType:
                // Add expected spanned list
                spannedList.add(Html.fromHtml("<h4 class=\\\"s2\\\">Nicodemus<\\/h4>"));
                spannedList.add(Html.fromHtml("<p class=\\\"p\\\"><sup id=\\\"John.3.1\\\" class=\\\"v\\\">1<\\/sup>There was a man of the Pharisees, named Nicodemus, a ruler of the Jews:<sup id=\\\"John.3.2\\\" class=\\\"v\\\">2<\\/sup>the same came to Jesus by night, and said unto him, Rabbi, we know that thou art a teacher come from God: for no man can do these miracles that thou doest, except God be with him.<sup id=\\\"John.3.3\\\" class=\\\"v\\\">3<\\/sup>Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.<\\/p>"));
                spannedList.add(Html.fromHtml("<p class=\\\"p\\\"><sup id=\\\"John.3.4\\\" class=\\\"v\\\">4<\\/sup>Nicodemus saith unto him, How can a man be born when he is old? can he enter the second time into his mother's womb, and be born?<sup id=\\\"John.3.5\\\" class=\\\"v\\\">5<\\/sup>Jesus answered, Verily, verily, I say unto thee, Except a man be born of water and <span class=\\\"add\\\">of<\\/span> the Spirit, he cannot enter into the kingdom of God.<sup id=\\\"John.3.6\\\" class=\\\"v\\\">6<\\/sup>That which is born of the flesh is flesh; and that which is born of the Spirit is spirit.<sup id=\\\"John.3.7\\\" class=\\\"v\\\">7<\\/sup>Marvel not that I said unto thee, Ye must be born again.<sup id=\\\"John.3.8\\\" class=\\\"v\\\">8<\\/sup>The wind bloweth where it listeth, and thou hearest the sound thereof, but canst not tell whence it cometh, and whither it goeth: so is every one that is born of the Spirit.<\\/p>"));
                break;
            case verseType:
                // Add expected spanned list
                spannedList.add(Html.fromHtml("1 John 4:18 <sup>18</sup>There is no <em>fear</em> in love; but perfect love casteth out <em>fear</em>: because <em>fear</em> hath torment. He that feareth is not made perfect in love."));
                spannedList.add(Html.fromHtml("Acts 2:43 <sup>43</sup>And <em>fear</em> came upon every soul: and many wonders and signs were done by the apostles."));
                break;
        }

        return spannedList;
    }

    /**
     * Creates test JSON version data
     *
     * @param successful Is success or failure expected?
     * @return JSON string
     */
    public static String createVersionJSON(boolean successful) {
        String jsonResponse = "";

        // https://bibles.org/v2/versions.js?language=eng-US
        if (successful) {
            jsonResponse = "{\"response\":{\"versions\":[{\"id\":\"eng-AMP\",\"name\":\"Amplified Bible\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.lockman.org\",\"audio\":\"NONE\",\"copyright\":\"\\n1954, 1958, 1962, 1964, 1965, 1987 by The Lockman Foundation\\n      \",\"info\":\"\\n        <h3>About this Title<\\/h3> <p>The Amplified Bible was the first Bible project of The Lockman Foundation. It attempts to take both word meaning and context into account in order to accurately translate the original text from one language into another. The Amplified Bible does this through the use of explanatory alternate readings and amplifications to assist the reader in understanding what Scripture really says. Multiple English word equivalents to each key Hebrew and Greek word clarify and amplify meanings that may otherwise have been concealed by the traditional translation method.<\\/p> <h3>Copyright Information<\\/h3> <p>All rights reserved. For Permission To Quote information visit <a href=\\\"http:\\/\\/www.lockman.org\\/\\\" target=\\\"_blank\\\">www.lockman.org<\\/a>.<\\/p> <p>The \\\"Amplified\\\" trademark is registered in the United States Patent and Trademark Office by The Lockman Foundation. Use of this trademark requires the permission of The Lockman Foundation.<\\/p>     \",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"AMP\",\"updated_at\":\"2013-05-22T10:39:36-05:00\"},{\"id\":\"eng-CEV\",\"name\":\"Contemporary English Version (US Version)\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.americanbible.org\\/\",\"audio\":\"NONE\",\"copyright\":\"<p>Contemporary English Version\\u00ae \\u00a9 1995 American Bible Society.  All rights reserved.<\\/p>\\n<p>Bible text from the Contemporary English Version (CEV) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 1865 Broadway, New York, NY 10023 (<a href=\\\"http:\\/\\/www.americanbible.org\\/\\\">www.americanbible.org<\\/a>).<\\/p>\",\"info\":\"<h1>Contemporary English Version (US Version)<\\/h1>\\n<p>Uncompromising simplicity marked the American Bible Society's translation of the Contemporary English Version that was first published in 1995. The text is easily read by grade schoolers, second language readers, and those who prefer the more contemporized form. The CEV is not a paraphrase; it is an accurate and faithful translation of the original manuscripts.<\\/p>\\n<p>After almost 200 years of ongoing ministry, American Bible Society invites people to experience the life-changing message of the Bible. Offering an increasing range of innovative ministries to address core life questions and struggles, the Bible Society partners with Christian churches and national Bible societies to share God\\u2019s Word both in the United States and around the globe.<\\/p>\\n<p>For more information, visit <a href=\\\"http:\\/\\/www.americanbible.org\\/\\\">www.americanbible.org<\\/a>.<\\/p> \\n<p>To purchase a copy of this translation, visit <a href=\\\"http:\\/\\/www.bibles.com\\/\\\">www.bibles.com<\\/a>.<\\/p>\",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"CEV\",\"updated_at\":\"2013-10-22T15:29:11-05:00\"},{\"id\":\"eng-CEVD\",\"name\":\"Contemporary English Version (US Version)\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.americanbible.org\\/\",\"audio\":\"NONE\",\"copyright\":\"<p>Contemporary English Version, Second Edition (CEV&#174;) &#169; 2006 American Bible Society. All rights reserved.<\\/p>\",\"info\":\"<h1>About this Title<\\/h1>\\n      <p>Uncompromising simplicity marked the American Bible Society's translation of the Contemporary English Version Bible that was first published in 1995. The text is easily read by grade schoolers, second language readers, and those who prefer the more contemporized form. The CEV is not a paraphrase. It is an accurate and faithful translation of the original manuscripts.<\\/p>\\n      <h2>Copyright Information<\\/h2>\\n      <p>Scriptures marked as \\\"(CEV)\\\" are taken from the Contemporary English Version Copyright &#169; 1995 by American Bible Society. Used by permission.<\\/p>\\n      <p>The text of the Contemporary English Version (CEV) appearing on or deriving from this or any other web page is for personal use only. The CEV text may be quoted in any form (written, visual, electronic or audio) up to &amp; inclusive of five hundred (500) verses without written permission, providing the verses quoted do not amount to fifty percent (50%) of a complete book of the Bible nor do the verses account for twenty-five percent (25%) or more of the total text of the work in which they are quoted.<\\/p>\\n      <p>This permission is contingent upon an appropriate copyright acknowledgement. Any use of the CEV shall be governed by above policy and shall be solely restricted to noncommercial, personal study purposes.<\\/p>\\n      <p>For any other use, please address your inquiries to the American Bible Society, 1865 Broadway, New York, NY 10023, Attn Permissions Dept;<\\/p>\\n      <p>CEV Home: http: <a href=\\\"http:\\/\\/www.americanbible.org\\/\\\">www.americanbible.org<\\/a>\\n      <\\/p>\",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"CEVD\",\"updated_at\":\"2013-10-22T09:02:45-05:00\"},{\"id\":\"eng-ESV\",\"name\":\"English Standard Version\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.crossway.org\",\"audio\":\"NONE\",\"copyright\":\"<p>Scripture quotations marked (ESV) are from The Holy Bible, English Standard Version\\u00ae, copyright \\u00a9 2001 by Crossway Bibles, a publishing ministry of Good News Publishers. Used by permission. All rights reserved.<\\/p>\\n  \",\"info\":\"<h1>English Standard Version<\\/h1>\\n<h3>Version Information<\\/h3>\\n<p>Published in 2001, the English Standard Version stands firmly in the King James tradition and is based directly on the 1971 Revised Standard Version (RSV).<\\/p>\\n    \\n    \",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"ESV\",\"updated_at\":\"2015-06-08T15:00:26-05:00\"},{\"id\":\"eng-GNTD\",\"name\":\"Good News Translation (US Version)\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.americanbible.org\\/\",\"audio\":\"NT\",\"copyright\":\"<p>Good News Translation (Today&#8217;s English Version, Second Edition) &#169; 1992 American Bible Society. All rights reserved.<\\/p>\",\"info\":\"<p>Good News Translation with Deuterocanonicals\\/Apocrypha. Scripture taken from the Good News Translation (r) (Today's English Version, Second Edition). Copyright (c) 1992 American Bible Society. Used by permission.<\\/p>\",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"GNTD\",\"updated_at\":\"2013-08-06T12:38:47-05:00\"},{\"id\":\"eng-KJVA\",\"name\":\"King James Version with Apocrypha, American Edition\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.americanbible.org\\/\",\"audio\":\"NONE\",\"copyright\":\"\\n      <p>King James Version 1611, spelling, punctuation and text formatting modernized by ABS in 1962; typesetting &#169; 2010 American Bible Society.<\\/p>\\n    \\n  \",\"info\":\"\\n      <h1>King James Version, American Edition<\\/h1>\\n      <p>King James Version 1611, spelling, punctuation and text formatting modernized by ABS in 1962; typesetting &#169; 2010 American Bible Society.<\\/p>\\n      <p>If you are interested in obtaining a printed copy, please visit the American Bible Society at <a href=\\\"http:\\/\\/www.bibles.com\\/\\\">www.bibles.com<\\/a>.<\\/p>\\n    \\n    \\n    \",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"KJVA\",\"updated_at\":\"2014-02-12T13:20:01-06:00\"},{\"id\":\"eng-MSG\",\"name\":\"The Message\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.messagebible.com\\/\",\"audio\":\"NONE\",\"copyright\":null,\"info\":\"\\n      <p>Why was <em>The Message<\\/em> written? The best answer to that question comes from Eugene Peterson himself: \\\"While I was teaching a class on Galatians, I began to realize that the adults in my class weren't feeling the vitality and directness that I sensed as I read and studied the New Testament in its original Greek. Writing straight from the original text, I began to attempt to bring into English the rhythms and idioms of the original language. I knew that the early readers of the New Testament were captured and engaged by these writings and I wanted my congregation to be impacted in the same way. I hoped to bring the New Testament to life for two different types of people: those who hadn't read the Bible because it seemed too distant and irrelevant and those who had read the Bible so much that it had become 'old hat.'\\\" <\\/p>\\n\\n      <p>Peterson's parishioners simply weren't connecting with the real meaning of the words and the relevance of the New Testament for their own lives. So he began to bring into English the rhythms and idioms of the original ancient Greek&#151;writing straight out of the Greek text without looking at other English translations. As he shared his version of Galatians with them, they quit stirring their coffee and started catching Paul's passion and excitement as he wrote to a group of Christians whom he was guiding in the ways of Jesus Christ. For more than two years, Peterson devoted all his efforts to <em>The Message New Testament<\\/em>. His primary goal was to capture the tone of the text and the original conversational feel of the Greek, in contemporary English.<\\/p>\\n\\n      <p>Language changes. New words are formed. Old words take on new meaning. There is a need in every generation to keep the language of the gospel message current, fresh, and understandable&#151;the way it was for its very first readers. That is what <em>The Message<\\/em> seeks to accomplish for contemporary readers. It is a version for our time&#151;designed to be read by contemporary people in the same way as the original <em> koine<\\/em> Greek and Hebrew manuscripts were savored by people thousands of years ago. <\\/p>\\n\\n      <p>That's why NavPress felt the time was right for a new version. When we hear something over and over again in the same way, we can become so familiar with it that the text loses its impact. The Message strives to help readers hear the living Word of God&#151;the Bible&#151;in a way that engages and intrigues us right where we are. <br><br>\\n\\n      <p>Some people like to read the Bible in Elizabethan English. Others want to read a version that gives a close word-for-word correspondence between the original languages and English. Eugene Peterson recognized that the original sentence structure is very different from that of contemporary English. He decided to strive for the spirit of the original manuscripts&#151;to express the rhythm of the voices, the flavor of the idiomatic expressions, the subtle connotations of meaning that are often lost in English translations.<\\/p>\\n\\n      <p>The goal of <em>The Message<\\/em> is to engage people in the reading process and help them understand what they read. This is not a study Bible, but rather \\\"a reading Bible.\\\" The verse numbers, which are not in the original documents, have been left out of the print version to facilitate easy and enjoyable reading. The original books of the Bible were not written in formal language. <em>The Message<\\/em> tries to recapture the Word in the words we use today. <\\/p>\\n    \",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"MSG\",\"updated_at\":\"2013-05-30T02:49:44-05:00\"},{\"id\":\"eng-NASB\",\"name\":\"New American Standard Bible\",\"lang\":\"eng-US\",\"lang_code\":\"ISO 639-3\",\"contact_url\":\"http:\\/\\/www.lockman.org\",\"audio\":\"NONE\",\"copyright\":\"\\n      New American Standard Bible, Copyright  1960,1962,1963,1968,1971,1972,1973,1975,1977,1995 by The Lockman Foundation.  Used by permission.\\n    \",\"info\":\"\\n      <h1>New American Standard Bible&#xAE;<\\/h1>\\n      <h2>SCRIPTURE PROMISE<\\/h2>\\n      <p><\\/p>\\n      <p>\\n        <em>\\\"The grass withers, the flower fades,<\\/em>\\n      <\\/p>\\n      <p>\\n        <em>but the word of our God stands forever.\\\"<\\/em>\\n      <\\/p>\\n      <p>ISAIAH 40:8<\\/p>\\n      <p><\\/p>\\n      <p>The New American Standard Bible&#xAE; has been produced with the conviction that the words of Scripture as originally penned in the Hebrew, Aramaic, and Greek were inspired by God.  Since they are the eternal Word of God, the Holy Scriptures speak with fresh power to each generation, to give wisdom that leads to salvation, that men may serve Christ to the glory of God.<\\/p>\\n      <p>The purpose of the Editorial Board in making this translation was to adhere as closely as possible to the original languages of the Holy Scriptures, and to make the translation in a fluent and readable style according to current English usage.<\\/p>\\n      <p><\\/p>\\n      <p><\\/p>\\n      <h2>THE FOURFOLD AIM OF THE LOCKMAN FOUNDATION<\\/h2>\\n      <p><\\/p>\\n      <p>1. These publications shall be true to the original Hebrew, Aramaic, and Greek.<\\/p>\\n      <p>2. They shall be grammatically correct.<\\/p>\\n      <p>3. They shall be understandable.<\\/p>\\n      <p>4. They shall give the Lord Jesus Christ His proper place, the place which the Word gives Him; therefore, no work will ever be personalized.<\\/p>\\n\\n      <h2>Copyright Information<\\/h2>\\n      <h3>New American Standard Bible&#xAE;<\\/h3>\\n      <p>Copyright &#xA9; 1960,1962,1963,1968,1971,1972,1973,1975,1977,1995 by<\\/p>\\n      <p>THE LOCKMAN FOUNDATION<\\/p>\\n      <p>A Corporation Not for Profit<\\/p>\\n      <p>LA HABRA, CA<\\/p>\\n      <p>All Rights Reserved<\\/p>\\n      <p>http:\\/\\/www.lockman.org<\\/p>\\n      <p><\\/p>\\n      <p>The \\\"NASB,\\\" \\\"NAS,\\\" \\\"New American Standard Bible,\\\" and \\\"New American Standard\\\" trademarks are registered in the United States Patent and Trademark Office by The Lockman Foundation.  Use of these trademarks requires the permission of The Lockman Foundation.<\\/p>\\n      <p><\\/p>\\n      <h2>PERMISSION TO QUOTE<\\/h2>\\n      <p>The text of the New American Standard Bible&#xAE; may be quoted and\\/or reprinted up to and inclusive of five hundred (500) verses without express written permission of The Lockman Foundation, providing the verses do not amount to a complete book of the Bible nor do the verses quoted account for more than 25% of the total work in which they are quoted.<\\/p>\\n      <p><\\/p>\\n      <p>Notice of copyright must appear on the title or copyright page of the work as follows:<\\/p>\\n      <p><\\/p>\\n      <p>\\\"Scripture taken from the NEW AMERICAN STANDARD BIBLE&#xAE;, Copyright &#xA9; 1960,1962,1963,1968,1971,1972,1973,1975,1977,1995 by The Lockman Foundation.  Used by permission.\\\"<\\/p>\\n      <p><\\/p>\\n      <p>When quotations from the NASB&#xAE; text are used in not-for-sale media, such as church bulletins, orders of service, posters, transparencies or similar media, the abbreviation (NASB) may be used at the end of the quotation.<\\/p>\\n      <p><\\/p>\\n      <p>This permission to quote is limited to material which is wholly manufactured in compliance with the provisions of the copyright laws of the United States of America.  The Lockman Foundation may terminate this permission at any time.<\\/p>\\n      <p><\\/p>\\n      <p>Quotations and\\/or reprints in excess of the above limitations, or other permission requests, must be directed to and approved in writing by The Lockman Foundation, PO Box 2279, La Habra, CA 90632-2279 (714)879-3055.<\\/p>\\n      <p>http:\\/\\/www.lockman.org<\\/p>\\n    \",\"lang_name\":\"English (US)\",\"lang_name_eng\":\"English\",\"abbreviation\":\"NASB\",\"updated_at\":\"2013-08-14T09:20:41-05:00\"}],\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'\\/\\/d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\\\"\\\\x3E\\\\x3C\\/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('55fa41fe6e3e21.58657713');\\\\x3C\\/script\\\\x3E\\\");\\n<\\/script><noscript><img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55fa41fe6e3e21.58657713\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/><\\/noscript>\",\"fums_tid\":\"55fa41fe6e3e21.58657713\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net\\/include\\/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55fa41fe6e3e21.58657713'); }\",\"fums_noscript\":\"<img src=\\\"https:\\/\\/d3a2okcloueqyx.cloudfront.net\\/nf1?t=55fa41fe6e3e21.58657713\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" \\/>\"}}}";
        } else {
            jsonResponse = "{ \"response\": { \"versions\": [ ], \"meta\": { \"fums\": \"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\x3Cscript src=\\\"'+document.location.protocol+'//d2ue49q0mum86x.cloudfront.net/include/fums.c.js\\\"\\x3E\\x3C/script\\x3E');}\\ndocument.write(\\\"\\x3Cscript\\x3E_BAPI.t('55fa41fe6e3e21.58657713');\\x3C/script\\x3E\\\");\\n</script><noscript><img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=55fa41fe6e3e21.58657713\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" /></noscript>\", \"fums_tid\": \"55fa41fe6e3e21.58657713\", \"fums_js_include\": \"d2ue49q0mum86x.cloudfront.net/include/fums.c.js\", \"fums_js\": \"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55fa41fe6e3e21.58657713'); }\", \"fums_noscript\": \"<img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=55fa41fe6e3e21.58657713\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" />\" } } }";
        }

        return jsonResponse;
    }

    /**
     * Creates test version list data
     *
     * @return Sample list of bible versions
     */
    public static List<BibleVersion> createVersionList() {
        List<BibleVersion> versionList = new LinkedList<>();

            versionList.add(new BibleVersion("eng-AMP", "Amplified Bible", "AMP"));
            versionList.add(new BibleVersion("eng-CEV", "Contemporary English Version (US Version)", "CEV"));
            versionList.add(new BibleVersion("eng-CEVD", "Contemporary English Version (US Version)", "CEVD"));
            versionList.add(new BibleVersion("eng-ESV", "English Standard Version", "ESV"));
            versionList.add(new BibleVersion("eng-GNTD", "Good News Translation (US Version)", "GNTD"));
            versionList.add(new BibleVersion("eng-KJVA", "King James Version with Apocrypha, American Edition", "KJVA"));
            versionList.add(new BibleVersion("eng-MSG", "The Message", "MSG"));
            versionList.add(new BibleVersion("eng-NASB", "New American Standard Bible", "NASB"));

        return versionList;
    }

    /**
     * Creates test JSON book data
     *
     * @param successful Is success or failure expected?
     * @return Sample bible book JSON string
     */
    public static String createBookJSON(boolean successful) {
        String jsonResponse = "";

        // https://bibles.org/v2/versions/eng-KJV/books.js
        if (successful) {
            jsonResponse = "{ \"response\": { \"books\": [ { \"version_id\": \"431\", \"name\": \"Genesis\", \"abbr\": \"Gen\", \"ord\": \"1\", \"book_group_id\": \"0\", \"testament\": \"OT\", \"id\": \"eng-KJV:Gen\", \"osis_end\": \"eng-KJV:Gen.50.26\", \"parent\": { \"version\": { \"path\": \"/versions/eng-KJV\", \"name\": \"King James Version\", \"id\": \"eng-KJV\" } }, \"next\": { \"book\": { \"path\": \"/books/eng-KJV:Exod\", \"name\": \"Exodus\", \"id\": \"eng-KJV:Exod\" } }, \"copyright\": \"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions © 2011 British and Foreign Bible Society; Crown Copyright in UK</p>\" }, { \"version_id\": \"431\", \"name\": \"John\", \"abbr\": \"John\", \"ord\": \"43\", \"book_group_id\": \"0\", \"testament\": \"NT\", \"id\": \"eng-KJV:John\", \"osis_end\": \"eng-KJV:John.21.25\", \"parent\": { \"version\": { \"path\": \"/versions/eng-KJV\", \"name\": \"King James Version\", \"id\": \"eng-KJV\" } }, \"next\": { \"book\": { \"path\": \"/books/eng-KJV:Acts\", \"name\": \"Acts\", \"id\": \"eng-KJV:Acts\" } }, \"previous\": { \"book\": { \"path\": \"/books/eng-KJV:Luke\", \"name\": \"Luke\", \"id\": \"eng-KJV:Luke\" } }, \"copyright\": \"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions © 2011 British and Foreign Bible Society; Crown Copyright in UK</p>\" }, { \"version_id\": \"431\", \"name\": \"Revelation\", \"abbr\": \"Rev\", \"ord\": \"66\", \"book_group_id\": \"0\", \"testament\": \"NT\", \"id\": \"eng-KJV:Rev\", \"osis_end\": \"eng-KJV:Rev.22.21\", \"parent\": { \"version\": { \"path\": \"/versions/eng-KJV\", \"name\": \"King James Version\", \"id\": \"eng-KJV\" } }, \"previous\": { \"book\": { \"path\": \"/books/eng-KJV:Jude\", \"name\": \"Jude\", \"id\": \"eng-KJV:Jude\" } }, \"copyright\": \"<p>King James Version 1611 (Authorized Version). Copyright status: UK English with BFBS additions © 2011 British and Foreign Bible Society; Crown Copyright in UK</p>\" } ], \"meta\": { \"fums\": \"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\x3Cscript src=\\\"'+document.location.protocol+'//d2ue49q0mum86x.cloudfront.net/include/fums.c.js\\\"\\x3E\\x3C/script\\x3E');}\\ndocument.write(\\\"\\x3Cscript\\x3E_BAPI.t('55fa2ef321a460.92471361');\\x3C/script\\x3E\\\");\\n</script><noscript><img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=55fa2ef321a460.92471361\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" /></noscript>\", \"fums_tid\": \"55fa2ef321a460.92471361\", \"fums_js_include\": \"d2ue49q0mum86x.cloudfront.net/include/fums.c.js\", \"fums_js\": \"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55fa2ef321a460.92471361'); }\", \"fums_noscript\": \"<img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=55fa2ef321a460.92471361\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" />\" } } }";
        } else {
            jsonResponse = "{ \"response\": { \"books\": [ ], \"meta\": { \"fums\": \"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\x3Cscript src=\\\"'+document.location.protocol+'//d2ue49q0mum86x.cloudfront.net/include/fums.c.js\\\"\\x3E\\x3C/script\\x3E');}\\ndocument.write(\\\"\\x3Cscript\\x3E_BAPI.t('55fa2ef321a460.92471361');\\x3C/script\\x3E\\\");\\n</script><noscript><img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=55fa2ef321a460.92471361\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" /></noscript>\", \"fums_tid\": \"55fa2ef321a460.92471361\", \"fums_js_include\": \"d2ue49q0mum86x.cloudfront.net/include/fums.c.js\", \"fums_js\": \"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('55fa2ef321a460.92471361'); }\", \"fums_noscript\": \"<img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=55fa2ef321a460.92471361\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" />\" } } }";
        }

        return jsonResponse;
    }

    /**
     * Creates a list of books for test data
     *
     * @return Sample bible books list
     */
    public static List<String> createBookList() {
        List<String> bookList = new LinkedList<>();

        bookList.add("Genesis");
        bookList.add("John");
        bookList.add("Revelation");

        return bookList;
    }

    @Test
    public void shouldSuccessfullyParseResponseStatusOK() {

        final String expectedException = "";
        String actualException = "";

        try {
            objectUnderTest.parseResponseStatus(BibleAPIResponse.RESPONSE_CODE_OK, BibleAPIResponse.RESPONSE_MESSAGE_OK);
        } catch (BiblecastException bsae) {
            actualException = bsae.getMessage();
        }

        assertEquals(expectedException, actualException);
    }

    @Test
    public void shouldSuccessfullyParseResponseStatusNotFound() {

        final String expectedException = BibleAPIResponse.RESPONSE_CODE_NOT_FOUND + " - " + BibleAPIResponse.RESPONSE_MESSAGE_NOT_FOUND;
        String actualException = "";

        try {
            objectUnderTest.parseResponseStatus(BibleAPIResponse.RESPONSE_CODE_NOT_FOUND, BibleAPIResponse.RESPONSE_MESSAGE_NOT_FOUND);
        } catch (BiblecastException bsae) {
            actualException = bsae.getMessage();
        }

        assertEquals(expectedException, actualException);
    }

    @Test
    public void shouldFindIncorrectTokenInResponseStatus() {

        final String expectedException = "Application API token is incorrect";
        String actualException = "";

        UIUtils.setContext(new Activity());

        try {
            objectUnderTest.parseResponseStatus(BibleAPIResponse.RESPONSE_CODE_UNAUTHORIZED, BibleAPIResponse.RESPONSE_MESSAGE_UNAUTHORIZED);
        } catch (BiblecastException bsae) {
            actualException = bsae.getMessage();
        }

        assertEquals(expectedException, actualException);
    }

    @Test
    public void shouldSuccessfullyParsePassagesJSONResultToList() {
        List<Spanned> actualList = new LinkedList<>();
        final String expectedException = "";
        String actualException = "";

        final List<Spanned> expectedList = createSearchList(passageType);
        final String jsonTextToParse = createSearchJSON(passageType, true);

        try {
            actualList = objectUnderTest.parseResponseDataToSpannedList(jsonTextToParse);
        } catch(BiblecastException bsae) {
            actualException = bsae.getMessage();
        }

        assertEquals(expectedException, actualException);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    @Test
    public void shouldFailToParsePassagesJSONResultToList() {
        List<Spanned> actualList = new LinkedList<>();
        final String expectedException = "No results found";
        String actualException = "";

        final List<Spanned> expectedList = createSearchList(passageType);
        final String jsonTextToParse = createSearchJSON(passageType, false);
        UIUtils.setContext(new Activity());

        try {
            actualList = objectUnderTest.parseResponseDataToSpannedList(jsonTextToParse);
        } catch(BiblecastException bsae) {
             actualException = bsae.getMessage();
        }

        assertEquals(expectedException, actualException);
        assertFalse(expectedList.size() == actualList.size());
        assertFalse(expectedList.toString().compareTo(actualList.toString()) == 0);
    }

    @Test
    public void shouldSuccessfullyParseVersesJSONResultToList() {
        List<Spanned> actualList = new LinkedList<>();
        final String expectedException = "";
        String actualException = "";

        final List<Spanned> expectedList = createSearchList(verseType);
        final String jsonTextToParse = createSearchJSON(verseType, true);

        try {
            actualList = objectUnderTest.parseResponseDataToSpannedList(jsonTextToParse);
        } catch(BiblecastException bsae) {
            actualException = bsae.getMessage();
        }

        assertEquals(expectedException, actualException);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    @Test
    public void shouldFailToParseVersesJSONResultToList() {
        List<Spanned> actualList = new LinkedList<>();
        final String expectedException = "No results found";
        String actualException = "";

        final List<Spanned> expectedList = createSearchList(verseType);
        final String jsonTextToParse = createSearchJSON(verseType, false);
        UIUtils.setContext(new Activity());

        try {
            actualList = objectUnderTest.parseResponseDataToSpannedList(jsonTextToParse);
        } catch(BiblecastException bsae) {
            actualException = bsae.getMessage();
        }

        assertEquals(expectedException, actualException);
        assertFalse(expectedList.size() == actualList.size());
        assertFalse(expectedList.toString().compareTo(actualList.toString()) == 0);
    }
}
