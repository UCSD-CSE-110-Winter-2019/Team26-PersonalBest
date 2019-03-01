package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class CompleteGoalUnitTest {
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
     * Tests whether toast appears when goal is completed
     */
    @Test
    public void testCompleteGoal() {
        activity.setStepCount(5000);
        assertEquals("5000/5000 steps today", textSteps.getText().toString());

        assertEquals("Congratulations, you've completed your goal of 5000 steps today!", ShadowToast.getTextOfLatestToast());
        assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().getDuration());

        activity.setStepCount(5000);
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext());
        settings.saveGoal(10000);

        activity.setStepCount(12000);

        assertEquals("Congratulations, you've completed your goal of 10000 steps today!", ShadowToast.getTextOfLatestToast());
        assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().getDuration());
    }

    /**
     * Tests whether toast does not appear when is goal is not completed
     */
    @Test
    public void testUncompletedGoal() {
        activity.setStepCount(5);
        assertNull(ShadowToast.getTextOfLatestToast());
    }

}