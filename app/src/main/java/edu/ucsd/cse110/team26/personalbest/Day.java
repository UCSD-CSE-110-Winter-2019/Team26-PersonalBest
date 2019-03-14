package edu.ucsd.cse110.team26.personalbest;

import java.util.Date;

class Day {
    long goal;
    long totalSteps;
    long walkSteps;
    long timeStamp;
    String dayID;
    Date date;

    public Day()
    {

    }

    public Day(int goal, int totalStep, int walkSteps, long timeStamp) {
        this.goal = goal;
        this.totalSteps = totalStep;
        this.walkSteps = walkSteps;
        this.timeStamp = timeStamp;
        this.dayID = new ConcreteTimeStamper().timestampToDayId(timeStamp);
    }
    public Day(int goal, int totalSteps, int walkSteps, String dayID, Date date)
    {
        this.goal = goal;
        this.totalSteps = totalSteps;
        this.walkSteps = walkSteps;
        this.dayID = dayID;
        this.date = date;
    }

    public Day(int goal, int totalSteps, int walkSteps, String dayId) {
        this.goal = goal;
        this.totalSteps = totalSteps;
        this.walkSteps = walkSteps;
        this.dayID = dayId;
        this.timeStamp = new ConcreteTimeStamper().dayIdToTimestamp(dayId);
    }

    public long getGoal() {
        return goal;
    }

    public long getTotalSteps() {
        return totalSteps;
    }

    public long getWalkSteps() {
        return walkSteps;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getDayID() {
        return dayID;
    }

    @Override
    public String toString() {
        return dayID + ": " + totalSteps + "/" + goal + " (walked: " + walkSteps + ")";
    }
}