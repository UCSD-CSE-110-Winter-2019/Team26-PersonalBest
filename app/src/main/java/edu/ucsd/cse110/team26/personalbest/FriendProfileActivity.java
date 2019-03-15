package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;

import java.util.ArrayList;
import java.util.List;

public class FriendProfileActivity extends AppCompatActivity {
    private TextView friendName;
    private TextView friendEmail;
    private boolean DEBUG;
    private IDataAdapter dataAdapter;
    private String email;
    private TimeStamper timeStamper;
    private List<Day> month;

    private CombinedChart monthChart;
    private BarChart createMonthChart;

    public User friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        DEBUG = getIntent().getExtras().getBoolean("DEBUG", false);
        dataAdapter = IDatabaseAdapterFactory.create(DEBUG, getApplicationContext());
        timeStamper = TimeStamperFactory.create(DEBUG);

        friendName = findViewById(R.id.friendName);
        friendEmail = findViewById(R.id.friendEmail);
        monthChart = findViewById(R.id.monthChart);

        email = getIntent().getExtras().getString("Friend Email");

        dataAdapter.getFriendDays(email, 28, (list) -> {
            month = new ArrayList<Day>();
            month.addAll(list);
            long ts;
            if( list.size() != 0 )
                ts = timeStamper.previousDay(timeStamper.startOfDay(month.get(month.size()-1).timeStamp));
            else
                ts = timeStamper.previousDay(timeStamper.startOfDay(timeStamper.now()));
            for(int i = month.size(); i < 28; i++ ) {
                month.add(new Day(5000, 0, 0, ts));
                ts = timeStamper.previousDay(ts);
            }
            createMonthChart = new BarChart(getApplicationContext(), monthChart, month);
            createMonthChart.draw();

        });

        Log.i(getClass().getSimpleName(), getIntent().getStringExtra("Friend Email"));
        dataAdapter.getFriend(email, (user) -> {
            friend = user.get(0);
            friendName.setText(friend.name);
            Log.d(getClass().getSimpleName(), "Chat ID: " + friend.chatID);
            dataAdapter.subscribeToChatNotifications(friend.chatID);
        });

        EditText message = findViewById(R.id.sendMsg);
        Button btnSendMsg = findViewById(R.id.btnSendMsg);
        btnSendMsg.setOnClickListener(view -> {
            dataAdapter.sendMessage(friend.chatID, message.getText().toString(), (success) -> {

                if(success) {
                    Log.d(getClass().getSimpleName(), "Sent message");
                    message.setText("");
                }
                else Log.d(getClass().getSimpleName(), "Failed to send message");
            });
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
                intent.putExtra("chat", friend.chatID);
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
}