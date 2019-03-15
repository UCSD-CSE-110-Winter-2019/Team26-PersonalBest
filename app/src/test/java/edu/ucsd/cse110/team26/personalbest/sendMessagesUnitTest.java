package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)

public class sendMessagesUnitTest {
    private FriendProfileActivity friendProfileActivity;

    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, FriendProfileActivity.class);
        intent.putExtra("DEBUG", true);
        friendProfileActivity = Robolectric.buildActivity(FriendProfileActivity.class, intent).create().get();
    }

    @Test
    public void sendMessage(){
        User testUser = new User(0, "name", "sally@gmail.com", "1");

    }

}
