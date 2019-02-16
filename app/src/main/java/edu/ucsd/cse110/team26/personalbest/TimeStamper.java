package edu.ucsd.cse110.team26.personalbest;

public interface TimeStamper {
    long now();
    long weekStart();
    long weekEnd();
    boolean isToday(long timeStamp);
    long endOfDay(long timeStamp);
    long nextDay(long timeStamp);
}
