package au.id.neasbey.biblecast;

import android.app.Activity;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.AutoCompleteTextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import au.id.neasbey.biblecast.GUIHelper.SearchSuggestionProvider;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by craigneasbey on 19/11/15.
 *
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchBibleAPITest {

    @Rule
    public ActivityTestRule<BibleSearch> mActivityRule = new ActivityTestRule<>(
            BibleSearch.class);

    private Activity mActivity = null;

    public static final String FIRST_STRING_TO_BE_TYPED = "jo";

    public static final String SECOND_STRING_TO_BE_TYPED = " 1";

    public static final String FIRST_SELECTED_STRING = "John";

    public static final String LAST_VERSE_IN_LIST = "43";

    public static final String BIBLE_VERSION="MSG";


    @Before
    public void setActivity() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void search_for_john_one() {

        // Type text into the search view
        onView(isAssignableFrom(AutoCompleteTextView.class))
                .perform(typeText(FIRST_STRING_TO_BE_TYPED));

        // Suggestions drop down displayed and John is in the list
        onData(CursorMatchers.withRowString(SearchSuggestionProvider.SUGGESTION, FIRST_SELECTED_STRING))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))) // on the pop-up window
                .check(matches(isDisplayed()));

        // Click John
        onData(CursorMatchers.withRowString(SearchSuggestionProvider.SUGGESTION, FIRST_SELECTED_STRING))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))) // on the pop-up window
                .perform(click());

        // Check John is in the search text
        onView(isAssignableFrom(AutoCompleteTextView.class))
                .check(matches(withText(FIRST_SELECTED_STRING)));

        // Type John 1 and search
        onView(isAssignableFrom(AutoCompleteTextView.class))
                .perform(typeText(SECOND_STRING_TO_BE_TYPED), pressImeActionButton());

        // Scroll to the bottom of the results list
        onData(hasToString(startsWith(LAST_VERSE_IN_LIST)))
                .inAdapterView(withId(R.id.resultView))
                .check(matches(isDisplayed()));

        // Click version list
        onView(withId(R.id.versionSpinner))
                .perform(click());

        // Suggestions drop down displayed and John is in the list
        onData(hasToString(startsWith(BIBLE_VERSION)))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))) // on the pop-up window
                .check(matches(isDisplayed()));

        // Click MSG
        onData(hasToString(startsWith(BIBLE_VERSION)))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))) // on the pop-up window
                .perform(click());

        // Scroll to the bottom of the results list
        onData(hasToString(startsWith(LAST_VERSE_IN_LIST)))
                .inAdapterView(withId(R.id.resultView))
                .check(matches(isDisplayed()));
    }

    /**
     * Google Cast device required
     */
    @Test
    public void display_on_google_cast() {
        // TODO Fix test

        //Select first available device

        // Once gesture view appears

        //Scroll down

        //Scroll up

        //Fling up

        //Fling down

        //Select version list

        //Select CEV

        //Change search test to "Jesus"

        //Click Enter

    }
}
