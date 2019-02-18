package edu.ucsd.cse110.team26.personalbest;

import java.util.Calendar;

public class TimeMachine {
    private static Calendar cal = Calendar.getInstance();

    public static long now() {
        return cal.getTimeInMillis();
    }

    public static void useFixedCalendar(Calendar newCal) {
        cal = newCal;
    }

    public static Calendar getCalendar() {
        return cal;
    }
}
