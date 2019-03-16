package edu.ucsd.cse110.team26.personalbest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)

public class ConcreteTimeStamperUnitTest {
    private ConcreteTimeStamper timeStamper = new ConcreteTimeStamper();

    @Test
    public void weekStartTest(){
        Calendar cal  = Calendar.getInstance(TimeZone.getDefault());
        // Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        assertEquals(cal.getTimeInMillis(), timeStamper.weekStart());

    }
    @Test
    public void weekEndTest(){
        Calendar cal  = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        cal.set(Calendar.DAY_OF_WEEK, 7);

        assertEquals(cal.getTimeInMillis(), timeStamper.weekEnd());
    }

    @Test
    public void nextDayTest(){
        assertEquals(24*60*60*1000, timeStamper.nextDay(0));
        assertEquals(24*60*60*1000 + 1, timeStamper.nextDay(1));

        assertEquals(48*60*60*1000, timeStamper.nextDay(24*60*60*1000));


    }
}