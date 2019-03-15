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
public class AddFriendUnitTest {
    private FriendsListActivity activity;
    private ListView list;
    private View itemView;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, FriendsListActivity.class);
        intent.putExtra("DEBUG", true);
        activity = Robolectric.buildActivity(FriendsListActivity.class, intent).create().get();
        list = activity.findViewById(R.id.list);
    }

    @Test
    public void testSendFriendRequest() {
        User testUser = new User(0, "name", "sally@gmail.com", "1");
        FriendsListActivity.friendsList.sentRequests.add(testUser);
        (FriendsListActivity.friendAdapter).notifyDataSetChanged();

        assertEquals(1, list.getAdapter().getCount());
        assertEquals(testUser, list.getAdapter().getItem(0));

        itemView = list.getAdapter().getView(0, null, new LinearLayout(activity));

        assertEquals("sally@gmail.com",  ((TextView) itemView.findViewById(R.id.friendEmail)).getText());
        assertEquals("name", ((TextView) itemView.findViewById(R.id.friendName)).getText());
        assertEquals("PENDING", ((TextView) itemView.findViewById(R.id.pendingLabel)).getText());

        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendEmail)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendName)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.pendingLabel)).getVisibility());
        assertEquals(View.GONE, ( itemView.findViewById(R.id.acceptFriendRequest)).getVisibility());
        assertEquals(View.GONE, ( itemView.findViewById(R.id.rejectFriendRequest)).getVisibility());
    }

    @Test
    public void testAcceptFriendRequest() {
        User testUser = new User(0, "name", "sally@gmail.com", "1");
        FriendsListActivity.friendsList.receivedRequests.add(testUser);
        (FriendsListActivity.friendAdapter).notifyDataSetChanged();
        assertEquals(1, list.getAdapter().getCount());
        assertEquals(testUser, list.getAdapter().getItem(0));

        itemView = list.getAdapter().getView(0, null, new LinearLayout(activity));

        assertEquals("sally@gmail.com", ((TextView) itemView.findViewById(R.id.friendEmail)).getText());
        assertEquals("name", ((TextView) itemView.findViewById(R.id.friendName)).getText());
        assertEquals("PENDING", ((TextView) itemView.findViewById(R.id.pendingLabel)).getText());

        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendEmail)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendName)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.pendingLabel)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.acceptFriendRequest)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.rejectFriendRequest)).getVisibility());

        FriendsListActivity.friendsList.receivedRequests.remove(testUser);
        FriendsListActivity.friendsList.friends.add(testUser);
        (FriendsListActivity.friendAdapter).notifyDataSetChanged();

        itemView = list.getAdapter().getView(0, null, new LinearLayout(activity));

        assertEquals("sally@gmail.com", ((TextView) itemView.findViewById(R.id.friendEmail)).getText());
        assertEquals("name", ((TextView) itemView.findViewById(R.id.friendName)).getText());

        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendEmail)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendName)).getVisibility());
        assertEquals(View.GONE, ( itemView.findViewById(R.id.pendingFriend)).getVisibility());
    }

    @Test
    public void testRejectFriendRequest() {
        User testUser = new User(0, "name", "sally@gmail.com", "1");
        FriendsListActivity.friendsList.receivedRequests.add(testUser);
        ((FriendListAdapter) FriendsListActivity.friendAdapter).notifyDataSetChanged();
        assertEquals(1, list.getAdapter().getCount());
        assertEquals(testUser, list.getAdapter().getItem(0));

        itemView = list.getAdapter().getView(0, null, new LinearLayout(activity));

        assertEquals("sally@gmail.com", ((TextView) itemView.findViewById(R.id.friendEmail)).getText());
        assertEquals("name", ((TextView) itemView.findViewById(R.id.friendName)).getText());
        assertEquals("PENDING", ((TextView) itemView.findViewById(R.id.pendingLabel)).getText());

        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendEmail)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.friendName)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.pendingLabel)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.acceptFriendRequest)).getVisibility());
        assertEquals(View.VISIBLE, ( itemView.findViewById(R.id.rejectFriendRequest)).getVisibility());

        FriendsListActivity.friendsList.receivedRequests.remove(testUser);
        ((FriendListAdapter) FriendsListActivity.friendAdapter).notifyDataSetChanged();

        assertEquals(0, list.getAdapter().getCount());
    }
}