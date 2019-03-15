package edu.ucsd.cse110.team26.personalbest;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatHistoryActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private IDataAdapter dataAdapter;
    private boolean DEBUG;
    private String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().getExtras() != null) {
            DEBUG = getIntent().getExtras().getBoolean("DEBUG", false);
            chatID = getIntent().getExtras().getString("chat");
        }
        dataAdapter = IDatabaseAdapterFactory.create(DEBUG, getApplicationContext());
        Log.d(TAG, "Chat ID: " + chatID);

        if(chatID.equals("")) {
            Log.e(TAG, "Chat id invalid");
            finish();
        }

        EditText message = findViewById(R.id.text_message);
        Button btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(view -> {
            dataAdapter.sendMessage(chatID, message.getText().toString(), (success) -> {
                if(success) {
                    Log.d(TAG, "Sent message");
                    message.setText("");
                }
                else Log.d(TAG, "Failed to send message");
            });
        });

        TextView chat = findViewById(R.id.chat);
        dataAdapter.startChatListener(chatID, (msg) -> {
            chat.append(msg.toString());
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
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
