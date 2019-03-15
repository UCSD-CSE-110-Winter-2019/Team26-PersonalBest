package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;

import java.util.ArrayList;
import java.util.List;

public class FriendProfileActivity extends AppCompatActivity {
    private TextView friendName;
    private TextView friendEmail;
    private BarChart barChart;
    private boolean DEBUG;
    private IDataAdapter dataAdapter;
    private String email;

    public User friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        DEBUG = getIntent().getExtras().getBoolean("DEBUG", false);
        dataAdapter = IDatabaseAdapterFactory.create(DEBUG, getApplicationContext());

        friendName = findViewById(R.id.friendName);
        friendEmail = findViewById(R.id.friendEmail);
        CombinedChart chart = findViewById(R.id.chart);
        List<Day> friendsData = getFriendsData();
        //barChart = new BarChart(getApplicationContext(), chart ,true);
        //barChart.draw(friendsData);

        email = getIntent().getExtras().getString("Friend Email");

        Log.i(getClass().getSimpleName(), getIntent().getStringExtra("Friend Email"));
        dataAdapter.getFriend(email, (user) -> {
            friend = user.get(0);
            friendName.setText(friend.name);
        });

        friendEmail.setText(email);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_chat_history:
                Intent intent = new Intent(FriendProfileActivity.this, ChatHistoryActivity.class);
                intent.putExtra("Friend Email", friend.email);
                intent.putExtra("DEBUG", DEBUG);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_friend_profile, menu);
        return true;
    }

    public List<Day> getFriendsData() {
        List<Day> data = new ArrayList<>();
        return data;
    }
}