package edu.ucsd.cse110.team26.personalbest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)

public class AlertDialogMessageUnitTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private StepCountActivity activity;
    private TextView textSteps;
    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getContext();
    }

    /**
     * Tests whether dialog box does not appear when goal is not completed
     */
    @Test
    public void testAlertDialogBeforeCompletedGoal() {
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        //intent.putExtra("DOCUMENT_KEY", " ");
        StepCountActivity activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        TextView textSteps = activity.findViewById(R.id.textSteps);
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);
        activity.setStepCount(1000);

        assertNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    /**
     * Tests whether dialog box appears when goal is completed
     */
    @Test
    public void testAlertDialogAfterCompletedGoal() {
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        //intent.putExtra("DOCUMENT_KEY", " ");
        StepCountActivity activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        TextView textSteps = activity.findViewById(R.id.textSteps);
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);

        activity.setStepCount(2000);

        assert(ShadowAlertDialog.getLatestAlertDialog().isShowing());


        //android.app.AlertDialog@58b97c15
    }

    /**
     * Tests whether dialog box appropriately modifies the goal in settings given that the user accepts the new goal.
     */
    @Test
    public void testAlertDialogAcceptNewGoal() {
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        //intent.putExtra("DOCUMENT_KEY", " ");
        StepCountActivity activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        TextView textSteps = activity.findViewById(R.id.textSteps);
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);

        activity.setStepCount(2000);

        ShadowAlertDialog.getLatestAlertDialog().getButton(Dialog.BUTTON_POSITIVE).performClick();
        assertEquals(2500, settings.getGoal());
    }

    /**
     * Tests whether dialog box doesn't modify the settings goal if the user doesn't accept.
     */
    @Test
    public void testAlertDialogDeclineNewGoal() {
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        //intent.putExtra("DOCUMENT_KEY", " ");
        StepCountActivity activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        TextView textSteps = activity.findViewById(R.id.textSteps);
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);

        activity.setStepCount(2000);

        ShadowAlertDialog.getLatestAlertDialog().getButton(Dialog.BUTTON_NEGATIVE).performClick();
        assertEquals(2000, settings.getGoal());

    }


}
