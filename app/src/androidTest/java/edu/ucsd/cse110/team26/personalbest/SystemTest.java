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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SystemTest {

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
     * Integration test that checks functionality of Milestone 1 features
     */
    @Test
    public void scenarioBasedSystemTest() {
        onView(withId(R.id.buttonGoToSteps)).perform(click());

        onView(withId(R.id.feet)).perform(new setValueNumberPicker(5));
        onView(withId(R.id.inch)).perform(new setValueNumberPicker(5));
        onView(withId(R.id.confirm))
                .perform(click());
        onView(withId(R.id.confirm))
                .perform(click());

        onView(withId(R.id.textSteps)).check(matches(isDisplayed()));
        onView(withId(R.id.textSteps)).check(matches(withText("0/5000 steps today")));
        onView(withId(R.id.btnStartWalk)).check(matches(withText("START WALK")));
        onView(withId(R.id.chart1)).check(matches(isDisplayed()));
        onView(withId(R.id.action_bar)).check(matches(isDisplayed()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

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
        onView(withId(R.id.goalEdit)).check(matches(withText("5000")));
        onView(withId(R.id.goalEdit)).perform(replaceText("1000"));
        onView(withId(R.id.btnSettingsSave)).perform(click());
        onView(withId(R.id.btnSettingsGoBack)).perform(click());


        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

        onView(withId(R.id.goalEdit)).check(matches(withText("1000")));
        onView(withId(R.id.btnSettingsGoBack)).perform(click());

        onView(withId(R.id.textSteps)).check(matches(withText("0/1000 steps today")));
        onView(withId(R.id.btnStartWalk)).perform((click()));
        onView(withId(R.id.btnStartWalk)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textWalkData)).check(matches(withText("Current walk:\nWalk duration: 0s\n0 steps taken\nDistance walked: 0.0 feet\nAverage speed: 0.0mph")));
        onView(withId(R.id.btnEndWalk)).check(matches(withText("END WALK")));
        onView(withId(R.id.chart1)).check(matches(isDisplayed()));
        onView(withId(R.id.btnEndWalk)).perform(click());
        onView(withId(R.id.chart1)).check(matches(isDisplayed()));

        mActivityTestRule.getActivity().finish();
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
