package au.id.neasbey.biblecast;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.AutoCompleteTextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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


    @Before
    public void setActivity() {
        mActivity = mActivityRule.getActivity();
    }

    /*
    Open Biblecast
    Type "jo" in the search text
    select John from suggestions drop down
    add " 1" to search text (eg. "John 1")
    click enter
    Wait for prompt to disappear
    Scroll to the bottom of the results list
    Click version list
    select MSG
    Once prompt has disappeared


    Google Cast device required

    Click cast icon
    Select first available device
    Once gesture view appears
    Scroll down
    Scroll up
    Fling up
    Fling down
    Select version list
    Select CEV
    Change search test to "Jesus"
    Click Enter

     */

    @Test
    public void search_for_john_one() {
        // TODO Needs to be fixed

        // Type text into the text view
        onView(withId(R.id.searchView))
                .perform(typeTextIntoFocusedView(FIRST_STRING_TO_BE_TYPED));

        //onView(withText(FIRST_SELECTED_STRING))
        //        .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
        //        .check(matches(isDisplayed()));

        onData(allOf(instanceOf(String.class), is(FIRST_SELECTED_STRING)))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());

        /*onData(hasValue(FIRST_SELECTED_STRING))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(scrollTo());
*/
        // Tap on a suggestion.
        /*
        onView(withText(FIRST_SELECTED_STRING))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());
*/
        /*
        // By clicking on the auto complete term, the text should be filled in.
        onView(withId(R.id.auto_complete_text_view))
                .check(matches(withText("South China Sea")));


        // Type search text.
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(replaceText(FIRST_STRING_TO_BE_TYPED));

        // Suggestions drop down displayed
        onView(isAssignableFrom(AutoCompleteTextView.class)).check(matches(isDisplayed()));

        // John is in the list
        onData(allOf(is(instanceOf(String.class)), withText("Hello!"))).check(matches(withText(FIRST_SELECTED_STRING)));

        // Click John
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(click(), typeText(FIRST_SELECTED_STRING));
        */

        // John is in the search text
        onView(withId(R.id.searchView)).check(matches(withText(FIRST_SELECTED_STRING)));

        // Type John 1 and search
        onView(withId(R.id.searchView)).perform(typeText(SECOND_STRING_TO_BE_TYPED), pressImeActionButton());
    }
}
