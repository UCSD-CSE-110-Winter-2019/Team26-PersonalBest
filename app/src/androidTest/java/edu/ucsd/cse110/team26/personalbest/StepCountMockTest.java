package edu.ucsd.cse110.team26.personalbest;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class StepCountMockTest {
    @Rule
    public ActivityTestRule<StepCountActivity> stepCountActivity = new ActivityTestRule<StepCountActivity>(StepCountActivity.class);

    @Test
    public void testGetCurrentResource()
    {
        LocalDateTime dummyTime = LocalDateTime.of(2017,2,7,00,00);
        TimeMachine.useFixedClockAt(dummyTime);
        String currentResource = stepCountActivity.getActivity().getCurrentStep();
        assertEquals(currentResource,"0");

       // mainActivity.getActivity().loadMedia(currentResource);
    }
}
