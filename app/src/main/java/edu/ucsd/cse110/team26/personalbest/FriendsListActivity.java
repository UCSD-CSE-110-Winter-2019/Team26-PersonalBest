package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private boolean DEBUG;
    private String newFriendEmail = null;
    IDataAdapter dataAdapter;
    ListView listView;
    public static List<User> friends;
    public static FriendListAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        friends = new ArrayList<User>();
        DEBUG = getIntent().getBooleanExtra("DEBUG", false);
        dataAdapter = IDatabaseAdapterFactory.create(DEBUG, FriendsListActivity.this);
        listView = (ListView) findViewById(R.id.list);
        friendAdapter = new FriendListAdapter(getApplicationContext(), dataAdapter);
        listView.setAdapter(friendAdapter);
        Log.i(getClass().getName(), "Attaching friendAdapter to FriendsListActivity");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AddFriendDialog addFriendDialog = new AddFriendDialog(FriendsListActivity.this, dataAdapter);
            addFriendDialog.createDialog().show();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.i(getClass().getName(), "Long click on friend at position" + position);
            User requested = (User) friendAdapter.getItem(position);
            RemoveFriendDialog removeFriendDialog = new RemoveFriendDialog(requested, FriendsListActivity.this, dataAdapter);
            return true;
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(getClass().getName(), "Click on friend at position" + position);
            User friend = (User) friendAdapter.getItem(position);
            Intent intent = new Intent(FriendsListActivity.this, FriendProfile.class);
            intent.putExtra("Friend Name", friend.name);
            startActivity(intent);
        });
    }

}
