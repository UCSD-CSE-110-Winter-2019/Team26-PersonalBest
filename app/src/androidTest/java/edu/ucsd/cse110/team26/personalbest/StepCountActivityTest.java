package edu.ucsd.cse110.team26.personalbest;


import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.NumberPicker;

import org.hamcrest.Matcher;
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
public class StepCountActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Tests whether UI components in StepCount Activity exist with the correct text
     */
    @Test
    public void stepCountActivityTest() {
        onView(withText("GOOGLE LOG IN")).check(matches(isDisplayed()));
        onView(withText("GOOGLE LOG IN")).perform(click());

        onView(withId(R.id.feet)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the value of a NumberPicker";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((NumberPicker)view).setValue(4);
            }
        });

        onView(withId(R.id.inch)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the value of a NumberPicker";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((NumberPicker)view).setValue(3);
            }
        });
        onView(withId(R.id.confirm))
                .perform(click());
        onView(withId(R.id.confirm))
                .perform(click());


        onView(withId(R.id.textSteps)).check(matches(isDisplayed()));
        onView(withId(R.id.btnStartWalk)).check(matches(withText("START WALK")));
        onView(withId(R.id.chart1)).check(matches(isDisplayed()));
        onView(withId(R.id.action_bar)).check(matches(isDisplayed()));
    }

}
