package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private boolean DEBUG;
    private String newFriendEmail = null;
    IDataAdapter dataAdapter;
    ListView listView;
    public static Friends friendsList;
    public static FriendListAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendsList = new Friends();

        Log.i(getClass().getName(), "Retrieving friends and requests from database to display");
        friendsList.receivedRequests = new ArrayList<User>();
        friendsList.sentRequests = new ArrayList<User>();
        friendsList.friends = new ArrayList<User>();

        DEBUG = getIntent().getExtras().getBoolean("DEBUG", false);
        dataAdapter = IDatabaseAdapterFactory.create(DEBUG, FriendsListActivity.this);
        listView = (ListView) findViewById(R.id.list);
        friendAdapter = new FriendListAdapter(getApplicationContext(), dataAdapter);
        listView.setAdapter(friendAdapter);

        dataAdapter.getReceivedFriendRequests( (List<User> receive) -> {
            friendsList.receivedRequests.addAll(receive);
            Log.i(getClass().getName(), "Count of received requests:" + receive.size());
            friendAdapter.notifyDataSetChanged();
        });

        dataAdapter.getSentFriendRequests( (List<User> send) -> {
            friendsList.sentRequests.addAll(send);
            Log.i(getClass().getName(), "Count of sent requests:" + send.size());
            friendAdapter.notifyDataSetChanged();
        });

        dataAdapter.getFriends( (List<User> friends) -> {
            friendsList.friends.addAll(friends);
            Log.i(getClass().getName(), "Count of friends:" + friends.size());
            friendAdapter.notifyDataSetChanged();
        });

        Log.i(getClass().getName(), "Attaching friendAdapter to FriendsListActivity");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AddFriendDialog addFriendDialog = new AddFriendDialog(FriendsListActivity.this, dataAdapter);
            addFriendDialog.createDialog().show();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.i(getClass().getName(), "Long click on friend at position" + position);
            User requested = (User) friendAdapter.getItem(position);
            if( friendsList.friends.contains(requested) ) {
                RemoveFriendDialog removeFriendDialog = new RemoveFriendDialog(requested, FriendsListActivity.this, dataAdapter);
                removeFriendDialog.createDialog().show();
            } else {
                Toast invToast = Toast.makeText(getApplicationContext(),"Selected item is not a friend", Toast.LENGTH_SHORT);
                invToast.show();
            }
            return true;
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(getClass().getName(), "Click on friend at position" + position);
            User friend = (User) friendAdapter.getItem(position);
            if( friendsList.friends.contains(friend) ) {
                Intent intent = new Intent(FriendsListActivity.this, FriendProfileActivity.class);
                intent.putExtra("Friend Email", friend.email);
                intent.putExtra("DEBUG", DEBUG);
                startActivity(intent);
            } else {
                Toast invToast = Toast.makeText(getApplicationContext(),"Selected item is not a friend", Toast.LENGTH_SHORT);
                invToast.show();
            }

        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
