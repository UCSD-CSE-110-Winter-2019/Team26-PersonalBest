package edu.ucsd.cse110.team26.personalbest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import static android.app.PendingIntent.getActivity;

public class NotificationsClass {//extends BroadcastReceiver {
    public NotificationsClass(){

    }
    public NotificationCompat.Builder createNotification(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "GoalNotifyID")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Goal Achieved!!")
                .setContentText("Go for 500 more steps!!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return builder;
    }

}
