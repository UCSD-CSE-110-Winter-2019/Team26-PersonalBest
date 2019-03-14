package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class TrackWalksUnitTest {

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
        intent.putExtra("ESPRESSO", false);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();

        textSteps = activity.findViewById(R.id.textSteps);
        textWalkData = activity.findViewById(R.id.textWalkData);
        btnStartWalk = activity.findViewById(R.id.btnStartWalk);
        btnEndWalk = activity.findViewById(R.id.btnEndWalk);
        nextStepCount = 1337;
    }

    @Test
    public void testSimpleWalk() {
        activity.setStepCount(nextStepCount);
        assertEquals("1337/5000 steps", textSteps.getText().toString());
        btnStartWalk.performClick();
        activity.setStepCount(nextStepCount+100);
        assertEquals("1437/5000 steps", textSteps.getText().toString());
        assertEquals("Current walk:\nWalk duration: 0s\n100 steps taken\nDistance walked: 0.0 feet\nAverage speed: 0.0mph", textWalkData.getText().toString());
        btnEndWalk.performClick();
        activity.updateWalkData();
    }

    @Test
    public void testLastWalk() {
        activity.setStepCount(0);
        assertEquals("0/5000 steps", textSteps.getText().toString());
        Settings settings = new Settings(activity.getApplicationContext(), new ConcreteTimeStamper());
        btnStartWalk.performClick();
        activity.setStepCount(100);
        assertEquals("100/5000 steps", textSteps.getText().toString());
        assertEquals("Current walk:\nWalk duration: 0s\n100 steps taken\nDistance walked: 0.0 feet\nAverage speed: 0.0mph", textWalkData.getText().toString());
        activity.fitnessService.updateStepCount(stepCount -> activity.setStepCount(stepCount));
        btnEndWalk.performClick();
        activity.walksToday = new ArrayList<Walk>();
        activity.walksToday.add(new Walk(100, Calendar.getInstance().getTimeInMillis()-5, Calendar.getInstance().getTimeInMillis()));
        activity.updateWalkData();
        assertEquals("Last walk:\nWalk duration: 0s\n100 steps taken\nDistance walked: 0.0 feet\nAverage speed: 0.0mph", textWalkData.getText().toString());
    }

    @Test
    public void testStartEndWalk() {
        assertEquals(View.VISIBLE, btnStartWalk.getVisibility());
        assertEquals(View.GONE, btnEndWalk.getVisibility());
        assertEquals("\n\n\n\n", textWalkData.getText());

        btnStartWalk.performClick();

        assertEquals(View.GONE, btnStartWalk.getVisibility());
        assertEquals(View.VISIBLE, btnEndWalk.getVisibility());
        assertNotNull(textWalkData.getText());

        btnEndWalk.performClick();

        assertEquals(View.VISIBLE, btnStartWalk.getVisibility());
        assertEquals(View.GONE, btnEndWalk.getVisibility());
    }

}