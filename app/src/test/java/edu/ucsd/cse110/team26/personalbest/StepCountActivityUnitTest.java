package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class StepCountActivityUnitTest {

    private StepCountActivity activity;
    private TextView textSteps;
    private TextView textWalkData;
    private Button btnStartWalk;
    private Button btnEndWalk;

    private long nextStepCount;

    @Before
    public void setUp() throws Exception {

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();

        textSteps = activity.findViewById(R.id.textSteps);
        textWalkData = activity.findViewById(R.id.textWalkData);
        btnStartWalk = activity.findViewById(R.id.btnStartWalk);
        btnEndWalk = activity.findViewById(R.id.btnEndWalk);
        nextStepCount = 1337;
    }

    @Test
    public void testUpdateSteps() {
        activity.setStepCount(nextStepCount);
        assertEquals("1337/5000 steps", textSteps.getText().toString());

        activity.setStepCount(nextStepCount+100);
        assertEquals("1437/5000 steps", textSteps.getText().toString());

        Settings settings = new Settings(InstrumentationRegistry.getInstrumentation().getContext(), new ConcreteTimeStamper());

        settings.saveGoal(10000);

        activity.setStepCount(nextStepCount+1000);
        assertEquals("2337/10000 steps", textSteps.getText().toString());
    }

}