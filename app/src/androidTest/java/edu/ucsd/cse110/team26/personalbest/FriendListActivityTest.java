package edu.ucsd.cse110.team26.personalbest;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.NumberPicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FriendListActivityTest {

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


        onView(withHint("Friend's Email Address")).inRoot(isDialog()).perform(typeText("sally@gmail.com"));
        onView(withText("Confirm")).inRoot(isDialog()).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.fab)).check(matches(isDisplayed()));


        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        ViewInteraction textView = onView(
                allOf(withId(R.id.pendingLabel), withText("PENDING"),
                        childAtPosition(
                                allOf(withId(R.id.pendingFriend),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                2)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("PENDING")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.friendName), withText("name"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("name")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.friendEmail), withText("sally@gmail.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list),
                                        0),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("sally@gmail.com")));




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