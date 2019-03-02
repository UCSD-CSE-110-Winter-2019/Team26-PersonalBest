package edu.ucsd.cse110.team26.personalbest;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GetHeightActivityTest {

    @Rule
    public ActivityTestRule<GetHeightActivity> mActivityTestRule = new ActivityTestRule<>(GetHeightActivity.class);

    /**
     * Tests whether UI components exists with the correct text
     */
    @Test
    public void getHeightActivityTest() {
        onView(withId(R.id.textView))
                .check(matches(withText("Please enter height")));
        onView(withId(R.id.confirm))
                .check(matches(withText("Confirm")));
        onView(withId(R.id.feetText))
                .check(matches(withText("Feet")));
        onView(withId(R.id.inchText))
                .check(matches(withText("Inch")));
        onView(withId(R.id.feet))
                .check(matches(isDisplayed()));
        onView(withId(R.id.inch))
                .check(matches(isDisplayed()));
        onView(withId(R.id.confirm))
                .perform(click());
        onView(withId(R.id.confirm))
                .check(matches(withText("Done")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
