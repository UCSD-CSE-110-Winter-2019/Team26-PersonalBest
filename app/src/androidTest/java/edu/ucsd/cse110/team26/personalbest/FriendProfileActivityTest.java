package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FriendProfileActivityTest {

    @Rule
    public ActivityTestRule<FriendProfileActivity> mActivityRule =
            new ActivityTestRule<>(FriendProfileActivity.class, false, false);


    @Test
    public void profileTest() {
        Intent i = new Intent();
        i.putExtra("Friend Email", "sally@gmail.com");
        i.putExtra("DEBUG", true);
        mActivityRule.launchActivity(i);

        onView(withId(R.id.friendEmail))
                .check(matches(withText("sally@gmail.com")));
        onView(withId(R.id.friendName)).check(matches(withText("name")));

        onView(withId(R.id.btnSendMsg)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSendMsg)).check(matches(withText("SEND")));

        onView(withId(R.id.sendMsg)).check(matches(isDisplayed()));
        onView(withId(R.id.sendMsg)).check(matches(withHint("Type a message")));

    }
}
