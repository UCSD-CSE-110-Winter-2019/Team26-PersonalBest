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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    /**
     * Tests whether UI components in Settings Activity exist with the correct text
     */
    @Test
    public void settingsActivityTest() {
        onView(withId(R.id.btnSettingsSave)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSettingsGoBack)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSettingsGoBack)).check(matches(withText("GO BACK")));
        onView(withId(R.id.btnSettingsSave)).check(matches(withText("SAVE")));

        onView(withId(R.id.goalEdit)).check(matches(isDisplayed()));
        onView(withId(R.id.feetNumberPicker)).check(matches(isDisplayed()));
        onView(withId(R.id.inchesNumberPicker)).check(matches(isDisplayed()));

        onView(withId(R.id.heightLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.heightLabel)).check(matches(withText("Height:")));
        onView(withId(R.id.goalLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.goalLabel)).check(matches(withText("Goal:")));

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
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
}