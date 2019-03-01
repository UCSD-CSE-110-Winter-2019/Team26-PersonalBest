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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class EncouragingMessageUnitTest {
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
     * Tests whether correct toast appears on the right conditions
     */
    @Test
    public void testEncouragingMessage() {
        activity.newDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(10000);
        activity.setStepCount(500);

        assertNull(ShadowToast.getTextOfLatestToast());

        activity.setStepCount(505);

        assertNull(ShadowToast.getTextOfLatestToast());

        activity.setStepCount(1000);
        assertEquals("Good job! You've improved by 100% from yesterday", ShadowToast.getTextOfLatestToast());
        assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().getDuration());

        activity.setStepCount(1500);
        assertEquals("Good job! You've improved by 200% from yesterday", ShadowToast.getTextOfLatestToast());
        assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().getDuration());

        activity.setStepCount(2000);
        assertEquals("Good job! You've improved by 300% from yesterday", ShadowToast.getTextOfLatestToast());
        assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().getDuration());
    }

    /**
     * Tests whether encouraging message does not appear when goal is completed
     */
    @Test
    public void testEncouragingMessageAfterCompletedGoal() {
        activity.newDay();
        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());
        settings.saveGoal(1000);
        activity.setStepCount(1000);

        assertNotEquals("Good job! You've improved by 200% from yesterday", ShadowToast.getTextOfLatestToast());

        activity.setStepCount(1500);
        assertNotEquals("Good job! You've improved by 300% from yesterday", ShadowToast.getTextOfLatestToast());

    }

}
