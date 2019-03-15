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
public class ChatHistoryActivityTest {

    @Rule
    public ActivityTestRule<ChatHistoryActivity> mActivityRule =
            new ActivityTestRule<>(ChatHistoryActivity.class, false, false);


    @Test
    public void chatHistoryTest() {
        Intent i = new Intent();
        i.putExtra("DEBUG", true);
        i.putExtra("chat", "test");
        mActivityRule.launchActivity(i);

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btn_send))
                .check(matches(withText("SEND")));
        onView(withId(R.id.btn_send))
                .check(matches(isDisplayed()));
        onView(withId(R.id.text_message)).check(matches(withHint("Type a message")));
        onView(withId(R.id.text_message)).check(matches(isDisplayed()));

        onView(withId(R.id.chat)).check(matches(withHint("message will appear here")));
        onView(withId(R.id.chat)).check(matches(isDisplayed()));

    }
}
