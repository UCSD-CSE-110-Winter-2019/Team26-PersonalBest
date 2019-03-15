package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)

public class SendMessagesUnitTest {
    private FriendProfileActivity friendProfileActivity;
    private ChatHistoryActivity activity;

    @Before
    public void setUp() throws Exception {

        Intent intent = new Intent(RuntimeEnvironment.application, FriendProfileActivity.class);
        intent.putExtra("DEBUG", true);

        friendProfileActivity = Robolectric.buildActivity(FriendProfileActivity.class, intent).create().get();
    }

    @Test
    public void testSendOneMessage() {

        User testUser = new User(0, "name", "bob@gmail.com", "1");

        EditText text = friendProfileActivity.findViewById(R.id.sendMsg);

        String message = "Hi";
        text.setText(message);

        friendProfileActivity.friend=testUser;
        friendProfileActivity.friend.chatID = "123";

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,message,(success)->{
            assert(success);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent2 = new Intent(RuntimeEnvironment.application, ChatHistoryActivity.class);
        intent2.putExtra("DEBUG", true);
        intent2.putExtra("chat", "123");
        activity = Robolectric.buildActivity(ChatHistoryActivity.class, intent2).create().get();

        TextView chatHistory = activity.findViewById(R.id.chat);

        assert(chatHistory.getText().toString().contains("(17970 days ago) Bob@gmail.com: Hi\n"));

    }

    @Test
    public void testSendMultipleMessages(){
        User testUser = new User(0, "name", "bob@gmail.com", "1");

        friendProfileActivity.friend=testUser;
        friendProfileActivity.friend.chatID = "123";

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"Hi",(success)->{

            assert(success);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"How are you?",(success)-> {
            assert (success);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"How's CSE 110?",(success)-> {
            assert (success);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"How are you?",(success)-> {
            assert (success);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        friendProfileActivity.dataAdapter.sendMessage(friendProfileActivity.friend.chatID ,"I heard it was really hard :(",(success)-> {
            assert (success);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent2 = new Intent(RuntimeEnvironment.application, ChatHistoryActivity.class);
        intent2.putExtra("DEBUG", true);
        intent2.putExtra("chat", "123");
        activity = Robolectric.buildActivity(ChatHistoryActivity.class, intent2).create().get();

        TextView chatHistory = activity.findViewById(R.id.chat);
        String expectedVal = "(17970 days ago) Bob@gmail.com: Hi\n" +
                "(17970 days ago) Bob@gmail.com: How are you?\n" +
                "(17970 days ago) Bob@gmail.com: How's CSE 110?\n" +
                "(17970 days ago) Bob@gmail.com: How are you?\n" +
                "(17970 days ago) Bob@gmail.com: I heard it was really hard :(\n";

        assert(chatHistory.getText().toString().contains(expectedVal));

    }

}
