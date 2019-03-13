package edu.ucsd.cse110.team26.personalbest;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import com.google.gson.annotations.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)

public class NotififcationsTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private StepCountActivity activity;
    private TextView textSteps;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();


        textSteps = activity.findViewById(R.id.textSteps);
    }


    //Test to see if notification doesn't appear when goal is not reached yet.
    @Test
    public void testNotificationBeforeCompletedGoal() {
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);
        activity.setStepCount(1000);

        NotificationManager notificationService = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        ShadowNotificationManager shadowNotificationManager = shadowOf(notificationService);
        assertEquals(0, shadowNotificationManager.size());

        activity.setStepCount(1999);

        assertEquals(0, shadowNotificationManager.size());


    }

    //Test to see if notification appears when goal is reached.
    @Test
    public void testNotificationShowsAfterCompletedGoal() {
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);
        activity.setStepCount(1000);

        NotificationManager notificationService = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        ShadowNotificationManager shadowNotificationManager = shadowOf(notificationService);

        assertEquals(0, shadowNotificationManager.size());
        settings.saveGoal(2000);
        activity.setStepCount(2000);


        assertEquals(1, shadowNotificationManager.size());

    }

    //Test to see if user is on screen when goal is reached; user will interact with pop-up dialog
    //if they interact with pop-up dialog, notification should disapper.
    @Test
    public void testNotificationDisappers(){
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);

        activity.setStepCount(2000);

        ShadowAlertDialog.getLatestAlertDialog().getButton(Dialog.BUTTON_NEGATIVE).performClick();
        NotificationManager notificationService = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        ShadowNotificationManager shadowNotificationManager = shadowOf(notificationService);

        assertEquals(0, shadowNotificationManager.size());

        activity.initializeNewDay();
        settings.saveGoal(1500);

        activity.setStepCount(1500);
        ShadowAlertDialog.getLatestAlertDialog().getButton(Dialog.BUTTON_POSITIVE).performClick();
        assertEquals(0, shadowNotificationManager.size());

    }






}
