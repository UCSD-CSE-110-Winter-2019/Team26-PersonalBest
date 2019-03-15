package edu.ucsd.cse110.team26.personalbest;


public interface TimeStamper {
    long now();
    long weekStart();
    long lastSevenDays();
    long lastTwentyEightDays();
    long today();
    long weekEnd();
    long[] getPreviousDay();
    int getDayOfWeek();
    boolean isToday(long timeStamp);
    long endOfDay(long timeStamp);
    long startOfDay(long timeStamp);
    long previousDay(long timeStamp);
    long nextDay(long timeStamp);
    String durationToString(long duration);
    String timestampToString(long timestamp);
    String timestampToDayId(long timestamp);
    long dayIdToTimestamp(String dayID);
    String[] listDay(int size);
    String getTargetID(int size);
}
