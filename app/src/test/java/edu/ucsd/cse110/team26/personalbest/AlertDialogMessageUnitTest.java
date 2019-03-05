package edu.ucsd.cse110.team26.personalbest;

import android.app.Dialog;
import android.content.Intent;
import android.widget.TextView;

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


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();


        textSteps = activity.findViewById(R.id.textSteps);
    }

    /**
     * Tests whether dialog box does not appear when goal is not completed
     */
    @Test
    public void testAlertDialogBeforeCompletedGoal() {
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
        activity.initializeNewDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(2000);

        activity.setStepCount(2000);

        ShadowAlertDialog.getLatestAlertDialog().getButton(Dialog.BUTTON_NEGATIVE).performClick();
        assertEquals(2000, settings.getGoal());

    }


}
