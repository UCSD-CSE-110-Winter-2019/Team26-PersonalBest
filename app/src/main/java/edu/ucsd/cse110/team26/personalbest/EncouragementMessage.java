package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.widget.Toast;

import java.util.Locale;

class EncouragementMessage {
    static void makeEncouragementMessage(Context context, int per) {
        String message = String.format(Locale.US, "Good job! You've improved by %d%% from yesterday", per);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
