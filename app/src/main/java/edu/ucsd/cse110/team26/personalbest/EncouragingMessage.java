package edu.ucsd.cse110.team26.personalbest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Locale;

public class EncouragingMessage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int improvementPercentage = intent.getIntExtra("percent", 0);
        if( improvementPercentage <= 0 )
            return;
        String message = String.format(Locale.US, "Good job! You've improved by %d%% from yesterday", improvementPercentage);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
