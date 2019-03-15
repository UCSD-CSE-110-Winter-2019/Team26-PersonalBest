package edu.ucsd.cse110.team26.personalbest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationsService extends FirebaseMessagingService {

    private final static String TAG = "FCMNotificationsService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String chatId = remoteMessage.getData().get("chat");
            String from = remoteMessage.getData().get("sender");
            Log.d(TAG, from + " " + chatId);
            if(chatId == null || from == null) return;

            String userEmail = getSharedPreferences("user", MODE_PRIVATE).getString("email", "");
            if(!userEmail.equals(from)) {
                showChatNotification(remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("body"),
                        chatId);
            } else {
                Log.d(TAG, "Notification was for message from self");
            }

        }

    }

    private void showChatNotification(String title, String body, String chatId) {
        Intent intent = new Intent(this, ChatHistoryActivity.class);
        intent.putExtra("chat", chatId);
        intent.putExtra("DEBUG", false);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(ChatHistoryActivity.class);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "CHAT")
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setGroup("CHAT")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("CHAT",
                    "Chat message notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(123, notification);
    }

}
