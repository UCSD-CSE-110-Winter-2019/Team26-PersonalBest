package edu.ucsd.cse110.team26.personalbest;

import java.util.Calendar;

public class MockTimeStamper implements TimeStamper {
    private static Calendar cal = Calendar.getInstance();

    @Override
    public long now() {
        return cal.getTimeInMillis();
    }

    @Override
    public int getDayOfWeek() { return 0;}

    @Override
    public long weekStart() {
        return 0;
    }

    @Override
    public long weekEnd() {
        return 0;
    }

    @Override
    public boolean isToday(long timeStamp) {
        return false;
    }

    @Override
    public long endOfDay(long timeStamp) {
        return 0;
    }

    @Override
    public long startOfDay(long timeStamp) {
        return 0;
    }

    @Override
    public long nextDay(long timeStamp) {
        return 1000;
    }

    @Override
    public String durationToString(long duration) {
        return null;
    }

}
