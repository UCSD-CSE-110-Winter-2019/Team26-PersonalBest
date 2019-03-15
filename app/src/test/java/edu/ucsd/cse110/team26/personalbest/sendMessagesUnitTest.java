package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.util.concurrent.RoboExecutorService;
import org.robolectric.shadows.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)

public class sendMessagesUnitTest {
    private FriendProfileActivity friendProfileActivity;
    private FriendsListActivity activity;
    private ListView list;
    private View itemView;


    @Before
    public void setUp() throws Exception {


        Intent intent = new Intent(RuntimeEnvironment.application, FriendProfileActivity.class);
        intent.putExtra("DEBUG", true);
        friendProfileActivity = Robolectric.buildActivity(FriendProfileActivity.class, intent).create().get();
    }

    @Test
    public void sendOneMessage(){

        User testUser = new User(0, "name", "bob@gmail.com", "1");

        EditText text = friendProfileActivity.findViewById(R.id.sendMsg);

        String message = "Hi";
        text.setText(message);

        friendProfileActivity.friend=testUser;
        friendProfileActivity.friend.chatID = "sally@gmail.com";

        System.out.println(friendProfileActivity.friend);

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,message,(success)->{
            assert(success);
        });

    }

    @Test
    public void sendMultipleMessages(){
        User testUser = new User(0, "name", "bob@gmail.com", "1");

        friendProfileActivity.friend=testUser;
        friendProfileActivity.friend.chatID = "sally@gmail.com";

        System.out.println(friendProfileActivity.friend);

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"Hi",(success)->{
            assert(success);
        });

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"How are you?",(success)-> {
            assert (success);
        });

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"How's CSE 110?",(success)-> {
            assert (success);
        });

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"I heard it was really hard :(",(success)-> {
            assert (success);
        });


    }

}
