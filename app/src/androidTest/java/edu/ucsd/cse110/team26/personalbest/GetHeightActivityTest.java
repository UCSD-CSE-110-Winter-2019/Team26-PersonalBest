package edu.ucsd.cse110.team26.personalbest;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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

}
