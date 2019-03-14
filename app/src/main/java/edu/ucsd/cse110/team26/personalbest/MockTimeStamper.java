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
    public long lastSevenDays() {return 0;}

    @Override
    public long lastTwentyEightDays() {return 0;}

    @Override
    public long today() {return 0;}

    @Override
    public long[] getPreviousDay() {
        return new long[0];
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
    public long previousDay(long timeStamp) {
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

    @Override
    public long dayIdToTimestamp(String dayID) {
        return 0;
    }

    @Override
    public String[] listDay(int size) {
        return new String[0];
    }

    @Override
    public String getTargetID(int size) {
        return null;
    }

    @Override
    public String timestampToDayId(long timestamp) {
        return null;
    }

}
