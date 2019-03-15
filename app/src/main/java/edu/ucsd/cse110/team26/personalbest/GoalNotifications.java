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
import android.support.v4.app.NotificationManagerCompat;

import static android.app.PendingIntent.getActivity;

public class GoalNotifications {//extends BroadcastReceiver {
    private Context context;
    private String channel_name = "GoalNotification";
    private String channel_description = "Notification channel for goal notifications";

    public GoalNotifications(Context context){
        this.context=context;
    }
    public NotificationCompat.Builder createNotification(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "GoalNotifyID")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Goal Achieved!!")
                .setContentText("Go for 500 more steps if you can!!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        return builder;
    }

    public void setNotification(Context context, NotificationCompat.Builder builder){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }
    public void showNotification(){
        NotificationCompat.Builder builder = createNotification(this.context);
        setNotification(this.context, builder);

    }


    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("GoalNotifyID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
