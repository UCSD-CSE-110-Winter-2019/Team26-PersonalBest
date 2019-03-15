package edu.ucsd.cse110.team26.personalbest;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.NumberPicker;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddFriendTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Activity mainActivity = mActivityTestRule.getActivity();
        SharedPreferences preferences = mainActivity.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Tests whether UI components in StepCount Activity exist with the correct text
     */
    @Test
    public void friendListActivityTest() {
        onView(withId(R.id.buttonGoToSteps)).perform(click());

        onView(withId(R.id.feet)).perform(new setValueNumberPicker(5));

        onView(withId(R.id.inch)).perform(new setValueNumberPicker(5));
        onView(withId(R.id.confirm))
                .perform(click());
        onView(withId(R.id.confirm))
                .perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Friend List")).perform(click());

        onView(withId(R.id.fab)).check(matches(isDisplayed()));

        onView(withId(R.id.fab)).perform(click());

        onView(withHint("Friend's Email Address")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Confirm")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Cancel")).inRoot(isDialog()).check(matches(isDisplayed()));

    }

    public class setValueNumberPicker implements ViewAction {
        int newVal = 0;
        setValueNumberPicker( int newVal ) {this.newVal = newVal;}

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
            ((NumberPicker)view).setValue(newVal);
        }

    }
}