package edu.ucsd.cse110.team26.personalbest;

class Day {
    long goal;
    long totalSteps;
    long walkSteps;
    long timeStamp;
    String dayId;

    public Day() {
        goal = 0;
        totalSteps = 0;
        walkSteps = 0;
        timeStamp = 0;
        dayId = "";
    }

    public Day(int goal, int totalStep, int walkSteps, long timeStamp) {
        this.goal = goal;
        this.totalSteps = totalStep;
        this.walkSteps = walkSteps;
        this.timeStamp = timeStamp;
        this.dayId = new ConcreteTimeStamper().timestampToDayId(timeStamp);
    }

    public Day(int goal, int totalSteps, int walkSteps, String dayId) {
        this.goal = goal;
        this.totalSteps = totalSteps;
        this.walkSteps = walkSteps;
        this.dayId = dayId;
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

    public String getDayId() {
        return dayId;
    }

    @Override
    public String toString() {
        return dayId + ": " + totalSteps + "/" + goal + " (walked: " + walkSteps + ")";
    }
}