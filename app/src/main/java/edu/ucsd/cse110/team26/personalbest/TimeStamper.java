package edu.ucsd.cse110.team26.personalbest;

import java.util.Calendar;

public interface TimeStamper {
    long now();
    Calendar getCalendar();
    long weekStart();
    long weekEnd();
    boolean isToday(long timeStamp);
    long endOfDay(long timeStamp);
}
