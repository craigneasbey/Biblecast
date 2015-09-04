package au.id.neasbey.biblecast.util;

import android.text.Html;
import android.text.Spanned;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import au.id.neasbey.biblecast.API.BibleOrg.BibleAPIResponseParserBibleOrgTest;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Test the string utilities
 */
public class HttpUtilsTest extends TestCase {

    private String createResultJSON() {
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"elements\" : [ \"");

        sb.append(HttpUtils.spannedToJSON(Html.fromHtml("<h4 class=\\\"s2\\\">Nicodemus<\\/h4>")));
        sb.append("\", \"");
        sb.append(HttpUtils.spannedToJSON(Html.fromHtml("<p class=\\\"p\\\"><sup id=\\\"John.3.1\\\" class=\\\"v\\\">1<\\/sup>There was a man of the Pharisees, named Nicodemus, a ruler of the Jews:<sup id=\\\"John.3.2\\\" class=\\\"v\\\">2<\\/sup>the same came to Jesus by night, and said unto him, Rabbi, we know that thou art a teacher come from God: for no man can do these miracles that thou doest, except God be with him.<sup id=\\\"John.3.3\\\" class=\\\"v\\\">3<\\/sup>Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.<\\/p>")));
        sb.append("\", \"");
        sb.append(HttpUtils.spannedToJSON(Html.fromHtml("<p class=\\\"p\\\"><sup id=\\\"John.3.4\\\" class=\\\"v\\\">4<\\/sup>Nicodemus saith unto him, How can a man be born when he is old? can he enter the second time into his mother's womb, and be born?<sup id=\\\"John.3.5\\\" class=\\\"v\\\">5<\\/sup>Jesus answered, Verily, verily, I say unto thee, Except a man be born of water and <span class=\\\"add\\\">of<\\/span> the Spirit, he cannot enter into the kingdom of God.<sup id=\\\"John.3.6\\\" class=\\\"v\\\">6<\\/sup>That which is born of the flesh is flesh; and that which is born of the Spirit is spirit.<sup id=\\\"John.3.7\\\" class=\\\"v\\\">7<\\/sup>Marvel not that I said unto thee, Ye must be born again.<sup id=\\\"John.3.8\\\" class=\\\"v\\\">8<\\/sup>The wind bloweth where it listeth, and thou hearest the sound thereof, but canst not tell whence it cometh, and whither it goeth: so is every one that is born of the Spirit.<\\/p>")));

        sb.append("\" ] }");

        return sb.toString();
    }

    private List<Spanned> createResultList() {
        List<Spanned> testList = new LinkedList<>();
        BibleAPIResponseParserBibleOrgTest.createJSONAndListHTML(testList, BibleAPIResponseParserBibleOrgTest.passageType, true);

        return testList;
    }

    public void testUrlEncodeUTF8Map() throws Exception {
        final String expected = "query=test%26query&version=test%3Cversion";

        Map<String, String> parameters = new HashMap<>();
        parameters.put("query", "test&query");
        parameters.put("version", "test<version");

        String actual = HttpUtils.urlEncodeUTF8(parameters);

        assertEquals(expected, actual);
    }

    public void testUrlEncodeUTF8() throws Exception {
        final String expected = "test%26query";

        String actual = HttpUtils.urlEncodeUTF8("test&query");

        assertEquals(expected, actual);
    }

    public void testListToJSON() {
        final String expectedJSON = createResultJSON();

        List<Spanned> elementsList = createResultList();
        String actualJSON = HttpUtils.listToJSON(elementsList);

        assertEquals(expectedJSON, actualJSON);
    }

    public void testSpannedToJSON() {
        final String expectedJSON = "<p dir=\\\"ltr\\\"><sup>1There was a man of the Pharisees, named Nicodemus, a ruler of the Jews:</sup><sup><sup>2the same came to Jesus by night, and said unto him, Rabbi, we know that thou art a teacher come from God: for no man can do these miracles that thou doest, except God be with him.</sup></sup><sup><sup><sup>3Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.</sup></sup></sup></p>";
        final String jsonHTML = "<p class=\\\"p\\\"><sup id=\\\"John.3.1\\\" class=\\\"v\\\">1<\\/sup>There was a man of the Pharisees, named Nicodemus, a ruler of the Jews:<sup id=\\\"John.3.2\\\" class=\\\"v\\\">2<\\/sup>the same came to Jesus by night, and said unto him, Rabbi, we know that thou art a teacher come from God: for no man can do these miracles that thou doest, except God be with him.<sup id=\\\"John.3.3\\\" class=\\\"v\\\">3<\\/sup>Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.<\\/p>";

        String actualJSON = HttpUtils.spannedToJSON(Html.fromHtml(jsonHTML));

        assertEquals(expectedJSON, actualJSON);
    }
}
