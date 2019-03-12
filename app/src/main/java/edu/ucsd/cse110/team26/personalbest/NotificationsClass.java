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

public class NotificationsClass extends BroadcastReceiver {
    public static boolean acceptedOrNO = false;
    public NotificationsClass(){

    }
    public NotificationCompat.Builder createNotification(Context context){
        Intent acceptAction = new Intent(context,NotificationsClass.class);
        acceptAction.setAction("acceptNewGoal");

        Intent rejectAction = new Intent(context,NotificationsClass.class);
        rejectAction.setAction("rejectNewGoal");
        //This is optional if you have more than one buttons and want to differentiate between two
//        intentAction.putExtra("userAction","acceptedNewGoal");
//        intentAction.putExtra("userAction", "rejectedNewGoal");

        Intent intent = new Intent(context, NotificationsClass.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = getActivity(context, 0, intent, 0);

        PendingIntent pIntentAccept = PendingIntent.getBroadcast(context, 1, acceptAction, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntentReject = PendingIntent.getBroadcast(context, 1, rejectAction, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification =  new NotificationCompat.Builder(context, "GoalNotifyID")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Goal Achieved!")
                .setContentText("Would you like to update your goal by 500 more steps?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                //Using this action button I would like to call logTest
                .addAction(R.mipmap.ic_launcher_round, "Accept", pIntentAccept)
                .addAction(R.mipmap.ic_launcher_round, "Reject", pIntentReject)

                //.setOngoing(true)
                .setAutoCancel(true);
        return notification;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        //System.out.println("NOOB");
        System.out.println(action);
        if(action.equals("acceptNewGoal")){
            System.out.println("accepted");
            acceptGoal();
        }
        else if(action.equals("rejectNewGoal")){
            rejectGoal();

        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
    public void acceptGoal(){
        acceptedOrNO = true;
    }
    public void rejectGoal(){
        acceptedOrNO = false;

    }

}
