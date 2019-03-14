package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class FriendProfileActivityUnitTest {
    private FriendProfileActivity activity;
    TextView friendEmail;

    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, FriendProfileActivity.class);
        intent.putExtra("DEBUG", true);
        intent.putExtra("Friend Email", "test@gmail.com");
        activity = Robolectric.buildActivity(FriendProfileActivity.class, intent).create().get();
        friendEmail = (TextView) activity.findViewById(R.id.friendEmail);
    }

    @Test
    public void testLabel() {
        assertEquals("test@gmail.com", friendEmail.getText());
    }

}
